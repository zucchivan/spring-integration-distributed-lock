CREATE TABLE context (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       type VARCHAR(255) NOT NULL,
       region VARCHAR(255) NOT NULL
);

CREATE TABLE attribute_map (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       xml_data BLOB NOT NULL,
       context_id INTEGER NOT NULL,
       FOREIGN KEY (context_id) REFERENCES context(id)
);