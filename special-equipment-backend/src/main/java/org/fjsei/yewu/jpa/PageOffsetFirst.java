package org.fjsei.yewu.jpa;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

//定制 PageOffsetFirst 以适应不指定页号的，而是指定已经显示的记录数的。
//原版来自spring-data-commons-2.1.4.RELEASE-sources.jar!/org/springframework/data/domain/PageRequest.java


public class PageOffsetFirst extends AbstractPageRequest {

    private static final long serialVersionUID = -4541509938956089562L;
    private final int  offset;       // page;
      //private final int size;
    private final Sort sort;

    /**
     * Creates a new {@link org.springframework.data.domain.PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
     * page.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @deprecated use {@link #of(int, int)} instead.
     */
    @Deprecated
    public PageOffsetFirst(int page, int size) {
        this(page, size, Sort.unsorted());
    }

    /**
     * Creates a new {@link org.springframework.data.domain.PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
     * @param properties the properties to sort by, must not be {@literal null} or empty.
     * @deprecated use {@link #of(int, int, Sort.Direction, String...)} instead.
     */
    @Deprecated
    public PageOffsetFirst(int page, int size, Sort.Direction direction, String... properties) {
        this(page, size, Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link org.springframework.data.domain.PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param sort can be {@literal null}.
     * @deprecated since 2.0, use {@link #of(int, int, Sort)} instead.
     */
      /*
    @Deprecated
    public PageOffsetFirst(int page, int size, Sort sort) {

        super(page, size);

        this.sort = sort;
    }
    */


    //映射offset ， page关系

    public PageOffsetFirst(int offset, int first, Sort sort) {

       // int page = offset/first;
        super(offset/first, first);
        this.offset =offset;

        this.sort = sort;
    }

    /**
     * Creates a new unsorted {@link org.springframework.data.domain.PageRequest}.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @since 2.0
     */
    public static PageOffsetFirst of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    /**
     * Creates a new {@link org.springframework.data.domain.PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param sort must not be {@literal null}.
     * @since 2.0
     */
    //修改
    public static PageOffsetFirst of(int offset, int first, Sort sort) {
        return new PageOffsetFirst(offset, first, sort);
    }

    /**
     * Creates a new {@link org.springframework.data.domain.PageRequest} with sort direction and properties applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @since 2.0
     */
    public static PageOffsetFirst of(int page, int size, Sort.Direction direction, String... properties) {
        return of(page, size, Sort.by(direction, properties));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getSort()
     */
    public Sort getSort() {
        return sort;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#next()
     */
    public Pageable next() {
        return new PageOffsetFirst(getPageNumber() + 1, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.AbstractPageRequest#previous()
     */
    public PageOffsetFirst previous() {
        return getPageNumber() == 0 ? this : new PageOffsetFirst(getPageNumber() - 1, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#first()
     */
    public Pageable first() {
        return new PageOffsetFirst(0, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PageOffsetFirst)) {
            return false;
        }

        PageOffsetFirst that = (PageOffsetFirst) obj;

        return super.equals(that) && this.sort.equals(that.sort);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Page request [offset: %d, first %d, sort: %s]", getOffset(), getPageSize(), sort);
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getOffset()
     */
    //重载
    @Override
    public long getOffset() {
        return (long) this.offset;
      // return (long) getPageNumber() * (long) getPageSize();
    }


}


