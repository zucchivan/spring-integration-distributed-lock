CREATE TABLE IF NOT EXISTS filtering_context (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       type VARCHAR(255) NOT NULL,
       region VARCHAR(255) NOT NULL
);

CREATE TABLE attribute_map_data (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       xml_data BLOB NOT NULL,
       filtering_context_id INTEGER NOT NULL,
       FOREIGN KEY (filtering_context_id) REFERENCES filtering_context(id)
);