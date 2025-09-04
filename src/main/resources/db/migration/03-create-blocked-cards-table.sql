CREATE TABLE IF NOT EXISTS bank.cards_to_block (
    id                bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    card_id           bigint NOT NULL REFERENCES bank.cards(id)
);

