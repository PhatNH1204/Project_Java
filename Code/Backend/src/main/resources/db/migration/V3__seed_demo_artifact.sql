INSERT INTO heritage_site (name, description, address)
SELECT 'Văn Miếu - Quốc Tử Giám',
       'Quần thể di tích lịch sử, văn hóa gắn với truyền thống giáo dục và khoa cử Việt Nam.',
       '58 Quốc Tử Giám, Đống Đa, Hà Nội'
WHERE NOT EXISTS (
    SELECT 1 FROM heritage_site WHERE name = 'Văn Miếu - Quốc Tử Giám'
);

INSERT INTO artifact (site_id, code, location_name)
SELECT site_id, 'VMQTG-KHVK-01', 'Khuê Văn Các'
FROM heritage_site
WHERE name = 'Văn Miếu - Quốc Tử Giám'
  AND NOT EXISTS (
      SELECT 1 FROM artifact WHERE code = 'VMQTG-KHVK-01'
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT artifact_id, 'vi', 'Khuê Văn Các',
       'Khuê Văn Các là công trình kiến trúc tiêu biểu trong quần thể Văn Miếu - Quốc Tử Giám. Hình ảnh gác Khuê Văn thường gắn với truyền thống hiếu học và văn hiến Thăng Long.'
FROM artifact
WHERE code = 'VMQTG-KHVK-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content
      WHERE artifact_id = artifact.artifact_id AND language_code = 'vi'
  );

INSERT INTO artifact_content (artifact_id, language_code, title, description)
SELECT artifact_id, 'en', 'Khue Van Pavilion',
       'Khue Van Pavilion is an iconic structure within the Temple of Literature complex in Hanoi. It is widely associated with the city''s scholarly and literary heritage.'
FROM artifact
WHERE code = 'VMQTG-KHVK-01'
  AND NOT EXISTS (
      SELECT 1 FROM artifact_content
      WHERE artifact_id = artifact.artifact_id AND language_code = 'en'
  );
