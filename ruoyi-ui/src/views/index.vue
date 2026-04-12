<template>
  <div class="home-container">
    <img src="@/assets/images/home-bg.jpeg" alt="首页" class="home-image" />
  </div>
</template>

<script>
export default {
  name: "Index"
}
</script>

<style scoped lang="scss">
.home-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;
  overflow: hidden;
}

.home-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
</style>