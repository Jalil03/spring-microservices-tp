DELETE FROM review;

INSERT INTO review (review_id, product_id, author, subject, content)
VALUES
    (1, 1, 'Alice', 'Très bon produit', 'Excellent pour la détection !'),
    (2, 1, 'Bob', 'Bon rapport qualité/prix', 'Assez efficace.'),
    (3, 2, 'Charlie', 'YOLOv5', 'Excellente performance.');
ALTER SEQUENCE review_seq RESTART WITH 4;
