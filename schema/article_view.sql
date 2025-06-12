create database article_view;

-- # 게시글 조회수 백업 용 테이블
create table article_view.article_view_count
(
    article_id bigint not null primary key,
    view_count bigint not null
);

create table article_view.outbox
(
    outbox_id  bigint        not null primary key,
    shard_key  bigint        not null,
    event_type varchar(100)  not null,
    payload    varchar(5000) not null,
    created_at datetime      not null
);

create index idx_shard_key_created_at on article_view.outbox (shard_key asc, created_at asc);