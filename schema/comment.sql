create database comment;

create table comment.comment (
    comment_id bigint NOT NULL PRIMARY KEY,
    content varchar(3000) NOT NULL,
    article_id bigint NOT NULL,
    parent_comment_id bigint NOT NULL,
    writer_id bigint NOT NULL,
    deleted boolean NOT NULL DEFAULT false,
    created_at datetime NOT NULL
);