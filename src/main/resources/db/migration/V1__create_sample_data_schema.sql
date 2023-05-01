CREATE TABLE attribute_map_data (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       xml_data BLOB NOT NULL
);

CREATE TABLE IF NOT EXISTS filtering_context (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       type VARCHAR(255) NOT NULL,
       region VARCHAR(255) NOT NULL,
       attribute_map_id INTEGER NOT NULL,
       FOREIGN KEY (attribute_map_id) REFERENCES attribute_map_data(id)
);