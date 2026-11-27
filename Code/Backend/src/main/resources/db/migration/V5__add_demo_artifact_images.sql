UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Constellation_of_Literature_pavilion_-_Temple_of_Literature,_Hanoi_-_DSC04553.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'VMQTG-KHVK-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:V%C4%83n_Mi%E1%BA%BFu,_%C4%90%E1%BA%A1i_trung_m%C3%B4n.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'VMQTG-DTM-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:V%C4%83n_mi%E1%BA%BFu,_Thi%C3%AAn_quang_t%E1%BB%89nh_v%C3%A0_%C4%90%E1%BA%A1i_th%C3%A0nh_m%C3%B4n.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'VMQTG-TQT-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:BiaTienSi1442.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'VMQTG-BTS-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Doan_Mon_Hanoi.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HTTL-DOANMON-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Dien_Kinh_Thien_001.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HTTL-KINHTHIEN-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:B%E1%BA%AFc_M%C3%B4n,_v%E1%BA%BFt_%C4%91%E1%BA%A1i_b%C3%A1c.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HTTL-CUABAC-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Ngo_Mon_Gate_(7351192986).jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HUE-NGOMON-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Dien_Thai_Hoa.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HUE-THAIHOA-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Th%E1%BA%BF_Mi%E1%BA%BFu,_Hu%E1%BA%BF.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HUE-THEMIEU-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Hoi_An,_Vietnam,_Japanese_Covered_Bridge.jpg'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HOIAN-CHUACAU-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Old_House_of_Tan_ky.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HOIAN-TANKY-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Phuc_Kien_hoi_quan.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'HOIAN-PHUKIEN-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:Thuong_temple.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'DENHUNG-DENTHUONG-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');

UPDATE artifact_content
SET image_url = 'https://commons.wikimedia.org/wiki/File:TrangAn02_DenTrinh.JPG'
WHERE artifact_id = (SELECT artifact_id FROM artifact WHERE code = 'TRANGAN-DENTRINH-01')
  AND language_code IN ('vi', 'en')
  AND (image_url IS NULL OR image_url = '');
