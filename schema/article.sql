create database article;

create table article.article
(
    article_id  bigint        NOT NULL PRIMARY KEY,
    title       varchar(100)  NOT NULL,
    content     varchar(3000) NOT NULL,
    board_id    bigint        NOT NULL,
    writer_id   bigint        NOT NULL,
    created_at  datetime      NOT NULL,
    modified_at datetime      NOT NULL
);

create index idx_board_id_article_id on article.article (board_id asc, article_id desc);

create table article.board_article_count
(
    board_id      bigint not null primary key,
    article_count bigint not null
);

create table article.outbox
(
    outbox_id  bigint        not null primary key,
    shard_key  bigint        not null,
    event_type varchar(100)  not null,
    payload    varchar(5000) not null,
    created_at datetime      not null
);

create index idx_shard_key_created_at on article.outbox (shard_key asc, created_at asc
