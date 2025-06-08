create database article_view;

# 게시글 조회수 백업 용 테이블
create table article_view.article_view_count
(
    article_id bigint not null primary key,
    view_count bigint not null
);