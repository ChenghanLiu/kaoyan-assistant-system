<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">讨论区</div>
        <div class="page-subtitle">支持发帖、详情、评论与本人删除</div>
      </div>
      <el-button type="primary" @click="dialogVisible = true">发布帖子</el-button>
    </div>
    <el-table :data="posts">
      <el-table-column prop="title" label="帖子标题" />
      <el-table-column prop="displayName" label="发布人" width="140" />
      <el-table-column prop="viewCount" label="浏览量" width="120" />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/student/posts/${row.id}`)">查看</el-button>
          <el-button
            v-if="canDelete(row)"
            link
            type="danger"
            @click="handleDelete(row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="发布帖子" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import { createPost, deletePost, fetchPosts } from '../../api/student'

const posts = ref([])
const dialogVisible = ref(false)
const form = reactive({ title: '', content: '' })
const authStore = useAuthStore()

const loadData = async () => {
  posts.value = await fetchPosts()
}

const canDelete = (row) => authStore.isAdmin || row.userId === authStore.user?.id

const handleCreate = async () => {
  await createPost(form)
  ElMessage.success('发布成功')
  dialogVisible.value = false
  Object.assign(form, { title: '', content: '' })
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该帖子吗？', '提示', { type: 'warning' })
  await deletePost(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
