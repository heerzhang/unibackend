package org.fjsei.yewu.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by yangjinfeng on 2017/1/5.
 */
public class CustomerCacheEventListener implements CacheEventListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${sei.cache.log:false}")
    private boolean  dolog;

    @Override
    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        if(dolog) logger.info("cache removed. key = {}, value = {}", element.getObjectKey(), element.getObjectValue());
    }

    @Override
    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        if(dolog) logger.info("cache put. key = {}, value = {}", element.getObjectKey(), element.getObjectValue());
    }

    @Override
    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        if(dolog) logger.info("cache updated. key = {}, value = {}", element.getObjectKey(), element.getObjectValue());
    }

    @Override
    public void notifyElementExpired(Ehcache ehcache, Element element) {
        if(dolog) logger.info("cache expired. key = {}, value = {}", element.getObjectKey(), element.getObjectValue());
    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        if(dolog) logger.info("cache evicted. key = {}, value = {}", element.getObjectKey(), element.getObjectValue());
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {
        if(dolog) logger.info("all elements removed. cache name = {}", ehcache.getName());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void dispose() {
        if(dolog)  logger.info("cache dispose.");
    }
}
