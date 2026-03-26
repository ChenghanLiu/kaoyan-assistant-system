<template>
  <div style="display:grid;gap:20px" v-if="post">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">{{ post.title }}</div>
          <div class="page-subtitle">作者：{{ post.displayName }} · 浏览量：{{ post.viewCount }} · 发布时间：{{ post.createdAt }}</div>
        </div>
        <el-button v-if="canDeletePost" link type="danger" @click="handleDeletePost">删除帖子</el-button>
      </div>
      <div style="line-height: 1.9; margin-top: 16px">{{ post.content }}</div>
    </div>
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">评论区</div>
          <div class="page-subtitle">同学们的讨论内容</div>
        </div>
      </div>
      <el-timeline>
        <el-timeline-item v-for="item in comments" :key="item.id" :timestamp="item.createdAt">
          <div style="display:flex;justify-content:space-between;gap:16px">
            <div>
              <strong>{{ item.displayName }}</strong>
              <div style="margin-top:6px">{{ item.content }}</div>
            </div>
            <el-button
              v-if="canDeleteComment(item)"
              link
              type="danger"
              @click="handleDeleteComment(item.id)"
            >
              删除
            </el-button>
          </div>
        </el-timeline-item>
      </el-timeline>
      <div style="margin-top: 20px; display: flex; gap: 12px">
        <el-input v-model="comment" type="textarea" :rows="3" placeholder="请输入评论内容" />
        <el-button type="primary" @click="handleComment">发表评论</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { createComment, deleteComment, deletePost, fetchComments, fetchPostDetail } from '../../api/student'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const post = ref(null)
const comments = ref([])
const comment = ref('')

const loadData = async () => {
  const [postData, commentData] = await Promise.all([
    fetchPostDetail(route.params.id),
    fetchComments(route.params.id)
  ])
  post.value = postData
  comments.value = commentData
}

const handleComment = async () => {
  await createComment(route.params.id, { content: comment.value })
  comment.value = ''
  ElMessage.success('评论成功')
  loadData()
}

const canDeletePost = computed(() => authStore.isAdmin || post.value?.userId === authStore.user?.id)

const canDeleteComment = (item) => authStore.isAdmin || item.userId === authStore.user?.id

const handleDeletePost = async () => {
  await ElMessageBox.confirm('确认删除该帖子吗？', '提示', { type: 'warning' })
  await deletePost(route.params.id)
  ElMessage.success('删除成功')
  router.push('/student/posts')
}

const handleDeleteComment = async (id) => {
  await ElMessageBox.confirm('确认删除该评论吗？', '提示', { type: 'warning' })
  await deleteComment(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
