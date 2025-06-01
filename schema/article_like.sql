create database article_like;

create table article_like.article_like
(
    article_like_id bigint   not null primary key,
    article_id      bigint   not null,
    user_id         bigint   not null,
    created_at      datetime not null
);

create unique index idx_article_id_user_id on article_like.article_like (article_id asc, user_id asc);

create table article_like.article_like_count
(
    article_id bigint not null primary key,
    like_count bigint not null,
    version    bigint not null
);

create table article_like.outbox
(
    outbox_id  bigint        not null primary key,
    shard_key  bigint        not null,
    event_type varchar(100)  not null,
    payload    varchar(5000) not null,
    created_at datetime      not null
);

create index idx_shard_key_created_at on article_like.outbox (shard_key asc, created_at asc);