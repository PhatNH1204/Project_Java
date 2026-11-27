INSERT INTO heritage_site (name, description, address)
SELECT 'Khu trung tâm Hoàng thành Thăng Long - Hà Nội',
       'Quần thể di tích gắn với lịch sử kinh đô Thăng Long và các triều đại Việt Nam.',
       '19C Hoàng Diệu, Ba Đình, Hà Nội'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Khu trung tâm Hoàng thành Thăng Long - Hà Nội'
);

INSERT INTO heritage_site (name, description, address)
SELECT 'Quần thể di tích Cố đô Huế',
       'Quần thể cung điện, thành quách, lăng tẩm và công trình lịch sử của triều Nguyễn.',
       'Thành phố Huế'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Quần thể di tích Cố đô Huế'
);

INSERT INTO heritage_site (name, description, address)
SELECT 'Đô thị cổ Hội An',
       'Đô thị thương cảng cổ với hệ thống nhà ở, hội quán, công trình tín ngưỡng và phố buôn bán truyền thống.',
       'Hội An, thành phố Đà Nẵng'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Đô thị cổ Hội An'
);

INSERT INTO heritage_site (name, description, address)
SELECT 'Khu di tích lịch sử Đền Hùng',
       'Quần thể di tích trên núi Nghĩa Lĩnh, gắn với tín ngưỡng thờ cúng các Vua Hùng.',
       'Khu di tích Đền Hùng, tỉnh Phú Thọ'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Khu di tích lịch sử Đền Hùng'
);

INSERT INTO heritage_site (name, description, address)
SELECT 'Khu di tích Thành Nhà Hồ',
       'Kinh thành đá quy mô lớn được xây dựng vào cuối thế kỷ XIV, tiêu biểu cho kỹ thuật xây thành bằng đá.',
       'Vĩnh Lộc, tỉnh Thanh Hóa'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Khu di tích Thành Nhà Hồ'
);

INSERT INTO heritage_site (name, description, address)
SELECT 'Quần thể danh thắng Tràng An',
       'Quần thể cảnh quan núi đá vôi, hang động, sông nước và di tích văn hóa tại Ninh Bình.',
       'Tỉnh Ninh Bình'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Quần thể danh thắng Tràng An'
);

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'VMQTG-DTM-01', 'Đại Trung Môn'
FROM heritage_site
WHERE name = 'Văn Miếu - Quốc Tử Giám'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'VMQTG-DTM-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'VMQTG-TQT-01', 'Thiên Quang Tỉnh'
FROM heritage_site
WHERE name = 'Văn Miếu - Quốc Tử Giám'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'VMQTG-TQT-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'VMQTG-BTS-01', 'Bia Tiến sĩ'
FROM heritage_site
WHERE name = 'Văn Miếu - Quốc Tử Giám'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'VMQTG-BTS-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HTTL-DOANMON-01', 'Đoan Môn'
FROM heritage_site
WHERE name = 'Khu trung tâm Hoàng thành Thăng Long - Hà Nội'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HTTL-DOANMON-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HTTL-KINHTHIEN-01', 'Nền điện Kính Thiên'
FROM heritage_site
WHERE name = 'Khu trung tâm Hoàng thành Thăng Long - Hà Nội'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HTTL-KINHTHIEN-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HTTL-CUABAC-01', 'Cửa Bắc'
FROM heritage_site
WHERE name = 'Khu trung tâm Hoàng thành Thăng Long - Hà Nội'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HTTL-CUABAC-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HUE-NGOMON-01', 'Ngọ Môn'
FROM heritage_site
WHERE name = 'Quần thể di tích Cố đô Huế'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HUE-NGOMON-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HUE-THAIHOA-01', 'Điện Thái Hòa'
FROM heritage_site
WHERE name = 'Quần thể di tích Cố đô Huế'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HUE-THAIHOA-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HUE-THEMIEU-01', 'Thế Miếu'
FROM heritage_site
WHERE name = 'Quần thể di tích Cố đô Huế'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HUE-THEMIEU-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HOIAN-CHUACAU-01', 'Chùa Cầu'
FROM heritage_site
WHERE name = 'Đô thị cổ Hội An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HOIAN-CHUACAU-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HOIAN-TANKY-01', 'Nhà cổ Tấn Ký'
FROM heritage_site
WHERE name = 'Đô thị cổ Hội An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HOIAN-TANKY-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'HOIAN-PHUKIEN-01', 'Hội quán Phúc Kiến'
FROM heritage_site
WHERE name = 'Đô thị cổ Hội An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'HOIAN-PHUKIEN-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'DENHUNG-DENHA-01', 'Đền Hạ'
FROM heritage_site
WHERE name = 'Khu di tích lịch sử Đền Hùng'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'DENHUNG-DENHA-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'DENHUNG-DENTRUNG-01', 'Đền Trung'
FROM heritage_site
WHERE name = 'Khu di tích lịch sử Đền Hùng'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'DENHUNG-DENTRUNG-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'DENHUNG-DENTHUONG-01', 'Đền Thượng'
FROM heritage_site
WHERE name = 'Khu di tích lịch sử Đền Hùng'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'DENHUNG-DENTHUONG-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'THANHHO-CONGNAM-01', 'Cổng Nam'
FROM heritage_site
WHERE name = 'Khu di tích Thành Nhà Hồ'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'THANHHO-CONGNAM-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'THANHHO-TUONGTHANH-01', 'Tường thành đá'
FROM heritage_site
WHERE name = 'Khu di tích Thành Nhà Hồ'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'THANHHO-TUONGTHANH-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'THANHHO-DANNAMGIAO-01', 'Đàn tế Nam Giao'
FROM heritage_site
WHERE name = 'Khu di tích Thành Nhà Hồ'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'THANHHO-DANNAMGIAO-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'TRANGAN-DENTRINH-01', 'Đền Trình'
FROM heritage_site
WHERE name = 'Quần thể danh thắng Tràng An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'TRANGAN-DENTRINH-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'TRANGAN-DENTRAN-01', 'Đền Trần'
FROM heritage_site
WHERE name = 'Quần thể danh thắng Tràng An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'TRANGAN-DENTRAN-01');

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'TRANGAN-HANGNAURUOU-01', 'Hang Nấu Rượu'
FROM heritage_site
WHERE name = 'Quần thể danh thắng Tràng An'
  AND NOT EXISTS (SELECT 1 FROM artifact WHERE code = 'TRANGAN-HANGNAURUOU-01');

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đại Trung Môn' AS title,
           'Đại Trung Môn là cổng nằm trên trục tham quan chính của Văn Miếu - Quốc Tử Giám, dẫn vào không gian nội tự và các công trình phía trong.'
           AS description
    UNION ALL
    SELECT 'en', 'Dai Trung Gate',
           'Dai Trung Gate stands on the main visitor axis of the Temple of Literature, leading from the outer grounds toward the inner monuments.'
) content
WHERE a.code = 'VMQTG-DTM-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Thiên Quang Tỉnh' AS title,
           'Thiên Quang Tỉnh là hồ nước vuông nằm giữa khuê Văn Các và khu bia Tiến sĩ, tạo khoảng mở trong không gian kiến trúc của di tích.'
           AS description
    UNION ALL
    SELECT 'en', 'Thien Quang Well',
           'Thien Quang Well is a square pond between Khue Van Pavilion and the Doctoral Stelae area, forming an open space within the monument complex.'
) content
WHERE a.code = 'VMQTG-TQT-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Bia Tiến sĩ' AS title,
           'Khu bia Tiến sĩ lưu giữ các bia đá ghi danh những người đỗ đại khoa qua các kỳ thi Nho học. Đây là tư liệu quan trọng về lịch sử giáo dục và khoa cử Việt Nam.'
           AS description
    UNION ALL
    SELECT 'en', 'Doctoral Stelae',
           'The Doctoral Stelae preserve stone records honoring scholars who passed the imperial examinations. They are an important source for the history of education in Vietnam.'
) content
WHERE a.code = 'VMQTG-BTS-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đoan Môn' AS title,
           'Đoan Môn là cổng phía nam dẫn vào khu vực trung tâm của Hoàng thành Thăng Long. Công trình nằm trên trục không gian quan trọng của khu di tích.'
           AS description
    UNION ALL
    SELECT 'en', 'Doan Mon Gate',
           'Doan Mon is the southern gate leading into the central area of the Thang Long Imperial Citadel. It stands on an important axis within the historic complex.'
) content
WHERE a.code = 'HTTL-DOANMON-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Nền điện Kính Thiên' AS title,
           'Nền điện Kính Thiên là dấu tích của chính điện trong Hoàng thành xưa. Bậc thềm đá chạm rồng phía trước là một trong những chi tiết kiến trúc nổi bật.'
           AS description
    UNION ALL
    SELECT 'en', 'Kinh Thien Palace Foundation',
           'The Kinh Thien Palace Foundation marks the site of a principal palace in the former citadel. Its carved stone dragon steps are a notable architectural feature.'
) content
WHERE a.code = 'HTTL-KINHTHIEN-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Cửa Bắc' AS title,
           'Cửa Bắc là một trong những cổng thành còn hiện diện tại khu trung tâm Hoàng thành Thăng Long. Công trình giúp hình dung cấu trúc phòng thủ của kinh thành qua các thời kỳ.'
           AS description
    UNION ALL
    SELECT 'en', 'Northern Gate',
           'The Northern Gate is one of the surviving gates in the central area of the Thang Long Imperial Citadel. It helps illustrate the citadel defenses of earlier periods.'
) content
WHERE a.code = 'HTTL-CUABAC-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Ngọ Môn' AS title,
           'Ngọ Môn là cổng chính phía nam của Hoàng thành Huế. Phía trên cổng là lầu Ngũ Phụng, tạo nên diện mạo đặc trưng của khu vực Đại Nội.'
           AS description
    UNION ALL
    SELECT 'en', 'Ngo Mon Gate',
           'Ngo Mon is the principal southern gate of the Imperial City of Hue. The Ngu Phung Pavilion above it forms one of the best-known views of the citadel.'
) content
WHERE a.code = 'HUE-NGOMON-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Điện Thái Hòa' AS title,
           'Điện Thái Hòa là công trình quan trọng trong Đại Nội Huế, từng được sử dụng cho các nghi lễ và hoạt động triều đình. Nội thất nổi bật với hệ khung gỗ và trang trí cung đình.'
           AS description
    UNION ALL
    SELECT 'en', 'Thai Hoa Palace',
           'Thai Hoa Palace is a major building in the Imperial City of Hue and was used for court ceremonies. Its interior features a timber structure and imperial decoration.'
) content
WHERE a.code = 'HUE-THAIHOA-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Thế Miếu' AS title,
           'Thế Miếu là nơi thờ các vua triều Nguyễn trong khu vực Hoàng thành Huế. Các án thờ và bài vị bên trong gắn với lịch sử hoàng gia Việt Nam.'
           AS description
    UNION ALL
    SELECT 'en', 'The To Mieu Temple',
           'The To Mieu Temple is dedicated to the emperors of the Nguyen dynasty within the Imperial City of Hue. Its altars and memorial tablets relate to Vietnam''s royal history.'
) content
WHERE a.code = 'HUE-THEMIEU-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Chùa Cầu' AS title,
           'Chùa Cầu là cây cầu có mái che nổi tiếng của Hội An, kết hợp công năng giao thông với không gian thờ tự. Công trình phản ánh dấu ấn giao lưu văn hóa của thương cảng xưa.'
           AS description
    UNION ALL
    SELECT 'en', 'Japanese Covered Bridge',
           'The Japanese Covered Bridge is a landmark of Hoi An, combining a passageway with a small place of worship. It reflects the cultural exchanges of the historic trading port.'
) content
WHERE a.code = 'HOIAN-CHUACAU-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Nhà cổ Tấn Ký' AS title,
           'Nhà cổ Tấn Ký là một ngôi nhà buôn truyền thống được bảo tồn trong khu phố cổ. Cấu trúc và cách bài trí cho thấy sự kết hợp của nhiều ảnh hưởng văn hóa tại Hội An.'
           AS description
    UNION ALL
    SELECT 'en', 'Tan Ky Old House',
           'Tan Ky Old House is a preserved traditional merchant home in the old town. Its structure and layout reflect the blend of cultural influences found in Hoi An.'
) content
WHERE a.code = 'HOIAN-TANKY-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Hội quán Phúc Kiến' AS title,
           'Hội quán Phúc Kiến là công trình cộng đồng của người Hoa gốc Phúc Kiến tại Hội An, đồng thời là nơi thờ Thiên Hậu. Cổng, sân và các chi tiết trang trí tạo nên không gian đặc sắc.'
           AS description
    UNION ALL
    SELECT 'en', 'Fujian Assembly Hall',
           'The Fujian Assembly Hall served the Fujianese Chinese community in Hoi An and is also dedicated to the sea goddess Thien Hau. Its gate, courtyard, and ornamentation form a distinctive complex.'
) content
WHERE a.code = 'HOIAN-PHUKIEN-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đền Hạ' AS title,
           'Đền Hạ nằm ở sườn núi Nghĩa Lĩnh trong quần thể Đền Hùng. Theo truyền thuyết, đây là nơi mẹ Âu Cơ sinh bọc trăm trứng, khởi nguồn câu chuyện về cộng đồng người Việt.'
           AS description
    UNION ALL
    SELECT 'en', 'Ha Temple',
           'Ha Temple stands on the slope of Nghia Linh Mountain in the Hung Kings complex. In legend, it is associated with Au Co and the story of the hundred children.'
) content
WHERE a.code = 'DENHUNG-DENHA-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đền Trung' AS title,
           'Đền Trung nằm trên tuyến hành hương lên núi Nghĩa Lĩnh. Không gian đền gắn với truyền thuyết về các Vua Hùng và tín ngưỡng tưởng nhớ tổ tiên của người Việt.'
           AS description
    UNION ALL
    SELECT 'en', 'Trung Temple',
           'Trung Temple lies along the pilgrimage route up Nghia Linh Mountain. The temple is connected with Hung Kings legends and Vietnamese traditions of honoring ancestors.'
) content
WHERE a.code = 'DENHUNG-DENTRUNG-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đền Thượng' AS title,
           'Đền Thượng ở vị trí cao trên núi Nghĩa Lĩnh, là điểm thờ tự quan trọng trong khu di tích Đền Hùng. Nơi đây gắn với nghi lễ tưởng niệm các Vua Hùng.'
           AS description
    UNION ALL
    SELECT 'en', 'Thuong Temple',
           'Thuong Temple occupies a high position on Nghia Linh Mountain and is an important place of worship in the Hung Kings complex. It is associated with ceremonies honoring the Hung Kings.'
) content
WHERE a.code = 'DENHUNG-DENTHUONG-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Cổng Nam Thành Nhà Hồ' AS title,
           'Cổng Nam là cổng chính của Thành Nhà Hồ, được ghép từ những khối đá lớn. Quy mô và kỹ thuật xây dựng thể hiện rõ đặc trưng kiến trúc của tòa thành.'
           AS description
    UNION ALL
    SELECT 'en', 'Southern Gate of Ho Citadel',
           'The Southern Gate is the principal gate of Ho Citadel and is built from massive stone blocks. Its scale and construction illustrate the citadel''s distinctive architecture.'
) content
WHERE a.code = 'THANHHO-CONGNAM-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Tường thành đá' AS title,
           'Tường thành được tạo nên từ các khối đá lớn xếp khít, tạo nên hệ thống phòng thủ bao quanh kinh thành. Đây là dấu tích tiêu biểu của kỹ thuật xây thành bằng đá tại Việt Nam.'
           AS description
    UNION ALL
    SELECT 'en', 'Stone Citadel Walls',
           'Large, closely fitted stone blocks form the defensive walls around the citadel. They are a notable example of stone fortification construction in Vietnam.'
) content
WHERE a.code = 'THANHHO-TUONGTHANH-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đàn tế Nam Giao' AS title,
           'Đàn tế Nam Giao là di tích nghi lễ gắn với việc tế trời đất của triều Hồ. Các nền đàn nhiều tầng cho thấy bố cục riêng của không gian tế lễ.'
           AS description
    UNION ALL
    SELECT 'en', 'Nam Giao Altar',
           'The Nam Giao Altar is a ritual site associated with ceremonies to Heaven and Earth under the Ho dynasty. Its tiered platforms reveal the layout of the ceremonial space.'
) content
WHERE a.code = 'THANHHO-DANNAMGIAO-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đền Trình' AS title,
           'Đền Trình là điểm thờ tự trong quần thể Tràng An, thường là nơi du khách dừng chân trước khi tiếp tục hành trình qua các tuyến sông và hang động.'
           AS description
    UNION ALL
    SELECT 'en', 'Trinh Temple',
           'Trinh Temple is a place of worship within the Trang An complex and is a common stopping point before visitors continue along the river and cave routes.'
) content
WHERE a.code = 'TRANGAN-DENTRINH-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Đền Trần' AS title,
           'Đền Trần nằm trong không gian cảnh quan Tràng An và là một điểm dừng văn hóa trên tuyến tham quan. Địa hình núi đá và rừng bao quanh tạo nên bối cảnh đặc trưng của di tích.'
           AS description
    UNION ALL
    SELECT 'en', 'Tran Temple',
           'Tran Temple is a cultural stop within the Trang An landscape. The surrounding limestone hills and woodland form a distinctive setting for the monument.'
) content
WHERE a.code = 'TRANGAN-DENTRAN-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT a.artifact_id, content.language_code, content.title, content.description
FROM artifact a
CROSS JOIN (
    SELECT 'vi' AS language_code, 'Hang Nấu Rượu' AS title,
           'Hang Nấu Rượu là một hang động trên tuyến tham quan Tràng An. Tên gọi gắn với câu chuyện dân gian về việc lấy nước trong hang để nấu rượu.'
           AS description
    UNION ALL
    SELECT 'en', 'Nau Ruou Cave',
           'Nau Ruou Cave is one of the caves on the Trang An visitor route. Local tradition links its name with stories of using water from the cave to make rice wine.'
) content
WHERE a.code = 'TRANGAN-HANGNAURUOU-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content existing
      WHERE existing.artifact_id = a.artifact_id
        AND existing.language_code = content.language_code
  );
