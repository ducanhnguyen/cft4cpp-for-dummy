package com.fit.cfg.object;

/**
 * Dạng câu lệnh đặc biệt dùng để đánh dấu phạm vi khả dụng của các khai báo bên
 * trong nó. <br/>
 * Trong ngôn ngữ lập trình, đó chính là cặp dấu ngoặc nhọn {}
 *
 * @author ducvu
 */
public class ScopeCfgNode extends CfgNode {

    /**
     * Mô tả rằng đang bắt đầu mở một khối câu lệnh
     */
    public static final String SCOPE_OPEN = "{";

    /**
     * Mô tả rằng đang đóng lại một khối câu lệnh
     */
    public static final String SCOPE_CLOSE = "}";

    /**
     * Khởi tạo câu lệnh với nội dung của nó
     *
     * @param content biểu diễn của phạm vi, bao gồm:
     *                <ul>
     *                <li>{@link #SCOPE_OPEN}</li>
     *                <li>{@link #SCOPE_CLOSE}</li>
     *                </ul>
     */
    public ScopeCfgNode(String content) {
        super(content);
    }

    /**
     * Khởi tạo câu lệnh với nội dung và câu lệnh tiếp theo của nó
     *
     * @param content biểu diễn của phạm vi, bao gồm:
     *                <ul>
     *                <li>{@link #SCOPE_OPEN}</li>
     *                <li>{@link #SCOPE_CLOSE}</li>
     *                </ul>
     * @param next    câu lệnh tiếp theo trong đường thi hành (cả 2 nhánh như nhau)
     * @see #setBranch(Statement, Statement)
     */
    public ScopeCfgNode(String content, CfgNode next) {
        super(content);
        setBranch(next);
    }

    /**
     * Tạo câu lệnh đóng khối mới
     *
     * @param branch 1 câu lệnh ở nhánh tiếp theo (tùy chọn)
     * @return câu lệnh đóng khối được tạo
     */
    public static ScopeCfgNode newCloseScope(ICfgNode... branch) {
        ScopeCfgNode close = new ScopeCfgNode(ScopeCfgNode.SCOPE_CLOSE);

        if (branch.length == 1)
            close.setBranch(branch[0]);
        return close;
    }

    /**
     * Tạo câu lệnh mở khối mới
     *
     * @param branch 1 câu lệnh ở nhánh tiếp theo (tùy chọn)
     * @return câu lệnh mở khối được tạo
     */
    public static ScopeCfgNode newOpenScope(CfgNode... branch) {
        ScopeCfgNode open = new ScopeCfgNode(ScopeCfgNode.SCOPE_OPEN);

        if (branch.length == 1)
            open.setBranch(branch[0]);

        return open;
    }

    @Override
    public boolean isNormalNode() {
        return false;
    }

    /**
     * Kiểm tra đây là câu lệnh mở khối, nếu không nó là câu lệnh đóng khối
     */
    public boolean isOpenScope() {
        return ScopeCfgNode.SCOPE_OPEN.equals(toString());
    }

    @Override
    public boolean shouldInBlock() {
        return true;
    }

}
