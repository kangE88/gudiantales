<route lang="yaml">
meta:
  id: usePointerSwipe
  title: usePointerSwipe 테스트
  menu: usePointerSwipe
  layout: SubLayout
  category: uiUtils
  publish: 이강
  publishVersion: 0.9
  header:
    variant: sub
    fixed: true
    showBack: true
    close: true
</route>

<template>
    <div
      ref="container"
      style="
        background-color: #e5e7eb;
        border-radius: 4px;
        position: relative;
        width: 100%;
        height: 80px;
        margin: auto;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
      "
    >
      <button @click="reset">
        Reset
      </button>
      <div
        ref="target"
        :style="{
          position: 'absolute',
          width: '100%',
          height: '100%',
          top: 0,
          right,
          backgroundColor: '#3eaf7c',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          opacity,
          transition: !isSwiping ? 'all 200ms linear' : 'none',
        }"
      >
        <p style="display: flex; color: white; align-items: center;">
          Swipe <mdi-arrow-right />
        </p>
      </div>
    </div>
  </template>
<script setup lang="ts">
import type { UseSwipeDirection } from '@vueuse/core'
import { usePointerSwipe } from '@vueuse/core'
import { computed, shallowRef, useTemplateRef } from 'vue'

const target = useTemplateRef('target')
const container = useTemplateRef('container')

const containerWidth = computed(() => container.value?.offsetWidth)

const right = shallowRef('20px')
const opacity = shallowRef(1)

function reset() {
  right.value = '20px'
  opacity.value = 1
}

const { distanceX, isSwiping } = usePointerSwipe(target, {
  disableTextSelect: true,
  onSwipe(e: PointerEvent) {
    if (containerWidth.value) {
      if (distanceX.value > 0) {
        const distance = Math.abs(distanceX.value)
        right.value = `${distance + 20}px`
        opacity.value = 1.25 - distance / containerWidth.value
      }
      else {
        right.value = '20px'
        opacity.value = 1
      }
    }
  },
  onSwipeEnd(e: PointerEvent, direction: UseSwipeDirection) {
    if (distanceX.value > 0 && containerWidth.value && (Math.abs(distanceX.value) / containerWidth.value) >= 0.5) {
      right.value = '100%'
      opacity.value = 0
    }
    else {
      right.value = '20px'
      opacity.value = 1
    }
  },
})
</script>

