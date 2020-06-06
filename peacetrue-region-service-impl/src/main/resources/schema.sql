DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`
(
    id        BIGINT AUTO_INCREMENT,
    code      VARCHAR(7)   NOT NULL COMMENT '编码',
    name      VARCHAR(255) NOT NULL COMMENT '名称',
    remark    VARCHAR(255) COMMENT '备注',
    parent_id BIGINT COMMENT '上一级主键',
    PRIMARY KEY (id)
);

COMMENT ON TABLE `region` IS '地区';
COMMENT ON COLUMN `region`.id IS '主键';

