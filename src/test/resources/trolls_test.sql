CREATE TABLE IF NOT EXISTS public.trolls
(
    id               serial       NOT NULL,
    "name"           varchar(50)  NOT NULL,
    description      varchar(150) NOT NULL,
    is_glitter_troll BOOLEAN      NOT NULL DEFAULT 'false',
    created_at       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT trolls_pkey PRIMARY KEY (id)
);

INSERT INTO public.trolls("name", description, is_glitter_troll)
VALUES ('Poppy', 'Poppy is the queen of the Pop Trolls.', false),
       ('Branch', 'Branch is the over-cautious paranoid survivalist in Troll Village.', false),
       ('Guy Diamond',
        'Guy Diamond is a glitter troll. Unlike most other trolls, Guy doesn''t wear clothes. Guy''s voice has an auto-tuned sound.',
        true),
       ('Biggie', 'Biggie is a big and fat troll.', false);
