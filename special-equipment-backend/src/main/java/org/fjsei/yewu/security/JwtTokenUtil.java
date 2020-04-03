package org.fjsei.yewu.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//这个类不能有私有数据=每个请求的不一样的状态不可保留。 OAuth2 规范=>OpenID Connect=>JSON Web Token JWT;
//目前的计算能力下，可以认为HMAC算法在“挑战/响应”身份认证应用中是安全的。

@Component
public class JwtTokenUtil implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";
    private static final long serialVersionUID = -3301605591108950415L;
    //    @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "It's okay here")
    private Clock clock = DefaultClock.INSTANCE;

    //配置文件中的密钥(非中文)，若修改了那么客户端必须删除token cookie，否则不可访问或等着证书过期。
    @Value("${jwt.secret}")
    private String secret;

    //时间设置要适当，秒=单位；
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${sei.cookie.domain:}")
    private final String  cookieDomain="";


    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        //这里HMAC using SHA-512 base64EncodedSecretKey 做Hash Base64Codec的密码；不支持汉字的;
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(createdDate)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();

        //算法选择：若是RS512， 签名验签，需提供RSA密钥对文件。
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
            && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
            username.equals(user.getUsername())
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }
    //根据token为其续命,更新对应的spring security;
    protected void continuedTokenLifeAuthentication(UserDetailsService userDetailsService, HttpServletRequest request, HttpServletResponse response, String token)
    {
        if(token == null )  	return;
        String username = null;
        final Claims claims = getAllClaimsFromToken(token);
        try {
                 //username = (Claims::getSubject).apply(claims);   为何手机电脑同时登录，token时间容易失效。
            username = claims.getSubject();      //绕了个大弯 (Claims::getSubject).apply(claims);
        } catch (IllegalArgumentException e) {
            logger.error("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            logger.warn("the token is expired and not valid anymore", e);
        }
        if (username == null )	    return;
        //用JwtUserDetailsService从数据库找的，　UserDetails这里就是JwtUser了;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);   //即时刷新UserDetails.enabled//=isEnabled()
     //todo: 登录已经过期了，应当抛出异常，否则返回都是null，不知道到底是何种情况内还是应用层没有数据。
        if(!validateClaims(claims, userDetails))         return;
        //用户已经被屏蔽了。
        if(!userDetails.isEnabled())        return;

       //给浏览器cookie.和token内部声称的时间不同，浏览器1.5个小时后就过期不给送了。
        if(needTokenToRefresh(claims))
            timeArrivedRegenerateToken(userDetails,request,response);

        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          // logger.info("authorizated user '{}', Reset security context", username);
             //实际就是userId保留到Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

    }

    //验证合法性的token; 而每一次登录都会重新生成token的。
    private Boolean validateClaims(Claims claims, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = claims.getSubject();      //绕了个大弯 (Claims::getSubject).apply(claims);
        final Date created = claims.getIssuedAt();   //getIssuedAtDateFromToken(token);
                                            //final Date expiration = getExpirationDateFromToken(token);
        Boolean isTokenExpired;
        final Date expiration = claims.getExpiration();
        isTokenExpired = expiration.before(clock.now());     //在now以前的
        //密码修改了？
        return (
                username.equals(user.getUsername())
                        && !isTokenExpired
                        && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }
    /*
    将jwt放到Cookie中反给用户并且使用HttpOnly属性来防止Cookie被JavaScript读取,并且设置一个过期时间来保证安全;
    */
    //时间快到了，重新发放证书
    private void timeArrivedRegenerateToken(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        String token = generateToken(userDetails);     //jwtTokenUtil.validateToken(authToken, userDetails)
        //浏览器自动遵守标准：超时的cookie就不会该送过来了。 那万一不守规矩？两手准备。
        Cookie cookie =new Cookie("token", token);
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(5400);      //这个时间和token内部声称的时间不同，这给浏览器用的 = 1.5个小时。
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("authorizated user '{}', timeArrivedRegenerateToken", userDetails.getUsername());
    }
    //计算更新时机,提前30分钟；
    private Boolean needTokenToRefresh(Claims claims) {
        final Date expiration = claims.getExpiration();
        Date  comp= new Date( expiration.getTime()-1000*60*30 );        //milliseconds since January 1, 1970
        return  comp.before(clock.now());
    }
}

