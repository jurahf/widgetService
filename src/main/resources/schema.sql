DROP TABLE IF EXISTS widget;

CREATE TABLE widget (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  x int not null,
  y int not null,
  z_index int not null,
  height int not null,
  width int not null,
  lastModificationDateTime DateTime not null
);