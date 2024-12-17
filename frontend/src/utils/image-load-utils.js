import { ref, getDownloadURL } from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';

/**
 * Firebase Storage에서 다운로드 URL 가져오기
 * @param {string} filePath - Firebase Storage 내 파일 경로 (예: 'uploads/image.png')
 * @returns {Promise<string>} - 다운로드 가능한 이미지 URL 반환
 */
export const fetchImageUrl = async (filePath) => {
  try {
    const storageRef = ref(firebaseStorage, filePath);
    const downloadUrl = await getDownloadURL(storageRef);
    return downloadUrl;
  } catch (error) {
    console.error('Firebase Storage 이미지 URL 가져오기 실패:', error);
    throw error; // 호출부에 오류를 던짐
  }
};
