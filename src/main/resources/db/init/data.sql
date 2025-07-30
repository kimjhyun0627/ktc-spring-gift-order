INSERT INTO PRODUCT (NAME, PRICE, IMAGE_URL, HIDDEN)
VALUES ('Sample1', 1000, 'http://example.com/1.png', FALSE),
       ('카카오가 들어간 항목', 1500, 'http://kakao.jpeg', TRUE),
       ('Sample2', 2000, 'http://example.com/2.png', FALSE),
       ('Sample3', 3000, 'http://example.com/3.png', TRUE);

INSERT INTO PRODUCT_OPTION (PRODUCT_ID, NAME, QUANTITY)
VALUES (1, '색상 레드', 10),
       (1, '색상 블루', 5),
       (2, '맛 카카오', 20),
       (2, '맛 바닐라', 1),
       (3, '용량 100ml', 15),
       (3, '용량 200ml', 8),
       (4, '버전 스탠다드', 12),
       (4, '버전 프리미엄', 6);
