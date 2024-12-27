<template>
  <div id="app">
    <h1>드래그 앤 드롭 폴더 업로드</h1>
    <div class="dropzone" @drop.prevent="handleDrop" @dragover.prevent>
      <p>여기에 폴더나 파일을 드래그하세요</p>
    </div>
    <div class="status">
      <p v-if="totalFolders">총 {{ totalFolders }}개 폴더 업로드</p>
      <p v-if="currentFolder">현재 폴더: {{ currentFolder }}</p>
      <p v-if="uploadProgress">업로드 진행: {{ uploadProgress }}%</p>
      <p v-if="currentFile">현재 파일: {{ currentFile }}</p>
    </div>
    <progress :value="uploadProgress" max="100"></progress>
  </div>
</template>

<script>
import { ref, uploadBytes } from "firebase/storage";
import { firebaseStorage } from "@/firebase/firebaseConfig";

export default {
  data() {
    return {
      totalFolders: 0,
      totalFiles: 0,
      uploadProgress: 0,
      currentFolder: "",
      currentFile: "",
    };
  },
  methods: {
    async handleDrop(event) {
      const items = event.dataTransfer.items;
      let folderFilesMap = {};

      for (let i = 0; i < items.length; i++) {
        const item = items[i].webkitGetAsEntry();
        if (item) {
          if (item.isDirectory) {
            folderFilesMap[item.name] = [];
            await this.traverseFileTree(item, folderFilesMap[item.name]);
          } else {
            console.warn(`폴더가 아닌 항목은 무시됨: ${item.name}`);
          }
        }
      }

      this.totalFolders = Object.keys(folderFilesMap).length;
      console.log("폴더별 파일 리스트:", folderFilesMap);

      // 폴더별로 순차적으로 업로드
      for (const [folderName, files] of Object.entries(folderFilesMap)) {
        this.currentFolder = folderName;
        this.totalFiles = files.length;
        await this.uploadFiles(folderName, files);
      }

      this.currentFolder = "모든 폴더 업로드 완료";
    },

    async traverseFileTree(item, fileList, path = "") {
      return new Promise((resolve, reject) => {
        if (item.isFile) {
          item.file((file) => {
            fileList.push({
              file,
              relativePath: path + file.name,
            });
            resolve();
          });
        } else if (item.isDirectory) {
          const reader = item.createReader();
          reader.readEntries(async (entries) => {
            for (let entry of entries) {
              await this.traverseFileTree(
                entry,
                fileList,
                path + item.name + "/"
              );
            }
            resolve();
          }, reject);
        }
      });
    },

    async uploadFiles(folderName, files) {
      let currentFileIndex = 0;

      for (const { file, relativePath } of files) {
        try {
          this.currentFile = `${folderName}/${relativePath}`;
          const storageRef = ref(
            firebaseStorage,
            `lookkit/products/${relativePath}`
          );
          await uploadBytes(storageRef, file);

          currentFileIndex++;
          this.uploadProgress = Math.round(
            (currentFileIndex / this.totalFiles) * 100
          );
        } catch (error) {
          console.error(`파일 업로드 실패: ${relativePath}`, error);
        }
      }

      this.uploadProgress = 100; // 업로드 완료
    },
  },
};
</script>

<style scoped>
.dropzone {
  width: 100%;
  height: 200px;
  border: 2px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  margin-bottom: 20px;
}
.status {
  margin-top: 20px;
  padding: 10px;
  background-color: #f0f0f0;
}
</style>
