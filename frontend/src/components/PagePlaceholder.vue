<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">{{ title }}</div>
        <div class="page-subtitle">{{ subtitle }}</div>
      </div>
    </div>
    <div class="placeholder-box">
      {{ description }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  subtitle: String,
  description: String
})
</script>
