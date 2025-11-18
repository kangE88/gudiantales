import { Meta } from '@storybook/blocks';

<Meta title="Composables/useSortable" />

# 🔄 useSortable

드래그 앤 드롭으로 리스트 정렬을 가능하게 하는 composable입니다.

## 개요

`useSortable`은 Sortable.js를 기반으로 한 VueUse의 useSortable을 래핑하여 간편하게 드래그 앤 드롭 정렬 기능을 추가할 수 있습니다.

### ✨ 주요 기능

- ✅ **드래그 앤 드롭 정렬** - 직관적인 리스트 재정렬
- ✅ **부드러운 애니메이션** - 기본 150ms 애니메이션 적용
- ✅ **자동 스크롤** - 가장자리에서 자동 스크롤 지원
- ✅ **다양한 옵션** - Handle, Filter, Group 등 다양한 커스터마이징
- ✅ **다중 리스트 지원** - 여러 리스트 간 항목 이동 가능

---

## 설치 및 Import

```typescript
import { useSortableList, useSortableCustom } from '@shc-nss/ui/solid/composables'
import { ref } from 'vue'
```

---

## 기본 사용법

### 1. useSortableList (권장)

기본 옵션이 적용된 간편한 버전입니다.

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { useSortableList } from '@/composables/useSortable'

const listRef = ref<HTMLElement>()
const items = ref([
  { id: 1, name: '항목 1' },
  { id: 2, name: '항목 2' },
  { id: 3, name: '항목 3' },
])

useSortableList(listRef, items)
</script>

<template>
  <ul ref="listRef">
    <li v-for="item in items" :key="item.id">
      {{ item.name }}
    </li>
  </ul>
</template>
```

### 2. useSortableCustom

모든 옵션을 직접 제어해야 하는 경우 사용합니다.

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { useSortableCustom } from '@/composables/useSortable'

const listRef = ref<HTMLElement>()
const items = ref([...])

useSortableCustom(listRef, items, {
  animation: 200,
  handle: '.drag-handle',
  ghostClass: 'sortable-ghost',
})
</script>
```

---

## API

### useSortableList

**매개변수**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|------|------|------|--------|------|
| `el` | `Ref<HTMLElement \| null \| undefined>` | ✅ | - | 정렬 가능하게 만들 요소의 ref |
| `list` | `Ref<T[]>` | ✅ | - | 정렬할 데이터 배열의 ref |
| `options` | `UseSortableOptions` | ❌ | - | Sortable.js 옵션 (기본값과 병합됨) |

**기본 옵션**
- `animation`: 150ms
- `scrollSensitivity`: 50px
- `scrollSpeed`: 20
- `scroll`: true
- `forceAutoScrollFallback`: true

### useSortableCustom

**매개변수**

| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `el` | `Ref<HTMLElement \| null \| undefined>` | ✅ | 정렬 가능하게 만들 요소의 ref |
| `list` | `Ref<T[]>` | ✅ | 정렬할 데이터 배열의 ref |
| `options` | `UseSortableOptions` | ✅ | Sortable.js 옵션 (기본값 없음) |

### DEFAULT_SORTABLE_OPTIONS

프로젝트 전체에서 사용할 수 있는 기본 옵션 상수입니다.

```typescript
export const DEFAULT_SORTABLE_OPTIONS: UseSortableOptions = {
  animation: 150,
  scrollSensitivity: 50,
  scrollSpeed: 20,
  scroll: true,
  forceAutoScrollFallback: true,
}
```

---

## 주요 옵션 가이드

### 1. 기본 옵션

```typescript
{
  animation: 150,           // 애니메이션 속도 (ms)
  scrollSensitivity: 50,    // 스크롤 시작 감도 (px)
  scrollSpeed: 20,          // 자동 스크롤 속도
}
```

### 2. Handle 옵션

특정 요소를 잡아야만 드래그 가능하도록 설정

```typescript
{
  handle: '.drag-handle',   // 드래그 핸들 CSS 선택자
}
```

```vue
<li v-for="item in items" :key="item.id">
  <span class="drag-handle">⋮⋮</span>
  {{ item.name }}
</li>
```

### 3. Filter 옵션

특정 항목의 드래그를 방지

```typescript
{
  filter: '.locked-item',   // 드래그 방지 CSS 선택자
}
```

```vue
<li v-for="item in items" :key="item.id" :class="{ 'locked-item': item.locked }">
  {{ item.name }}
</li>
```

### 4. Disabled 옵션

드래그 기능을 동적으로 활성화/비활성화

```typescript
const isEditMode = ref(false)

useSortableList(listRef, items, {
  disabled: computed(() => !isEditMode.value)
})
```

### 5. Group 옵션

여러 리스트 간 항목 이동 설정

```typescript
// 기본 그룹
{
  group: 'shared'
}

// 고급 그룹 설정
{
  group: {
    name: 'shared',
    pull: true,           // 다른 리스트로 이동 가능
    put: true,            // 다른 리스트에서 받기 가능
  }
}

// 복사 모드
{
  group: {
    name: 'shared',
    pull: 'clone',        // 복사본 생성
    put: false,           // 받기 불가
  }
}
```

### 6. Direction 옵션

정렬 방향 설정

```typescript
{
  direction: 'vertical',    // 세로 (기본값)
  // 또는
  direction: 'horizontal',  // 가로
}
```

### 7. Delay 옵션

드래그 시작 지연 (실수 방지)

```typescript
{
  delay: 200,               // 200ms 후 드래그 시작
}
```

---

## 이벤트 핸들러

### 주요 이벤트

```typescript
useSortableList(listRef, items, {
  onStart: (evt) => {
    console.log('드래그 시작', evt)
  },
  onEnd: (evt) => {
    console.log('드래그 종료', evt)
  },
  onUpdate: (evt) => {
    console.log('순서 변경', evt)
  },
  onAdd: (evt) => {
    console.log('항목 추가됨', evt)
  },
  onRemove: (evt) => {
    console.log('항목 제거됨', evt)
  },
})
```

### 이벤트 목록

- `onStart`: 드래그 시작 시
- `onEnd`: 드래그 종료 시
- `onAdd`: 다른 리스트에서 항목 추가 시
- `onUpdate`: 리스트 내 항목 순서 변경 시
- `onRemove`: 다른 리스트로 항목 이동 시
- `onSort`: 정렬 발생 시
- `onFilter`: 필터링된 항목 클릭 시
- `onMove`: 드래그 중 이동 시마다 호출 (false 반환 시 이동 취소)

---

## 고급 사용 예제

### 1. Handle과 Filter 조합

```vue
<script setup lang="ts">
const items = ref([
  { id: 1, name: '항목 1', locked: false },
  { id: 2, name: '항목 2', locked: true },
  { id: 3, name: '항목 3', locked: false },
])

useSortableList(listRef, items, {
  handle: '.drag-handle',
  filter: '.locked-item',
})
</script>

<template>
  <ul ref="listRef">
    <li v-for="item in items" :key="item.id" :class="{ 'locked-item': item.locked }">
      <span v-if="!item.locked" class="drag-handle">⋮⋮</span>
      <span v-else>🔒</span>
      {{ item.name }}
    </li>
  </ul>
</template>
```

### 2. 다중 리스트 간 이동

```vue
<script setup lang="ts">
const todoItems = ref([...])
const doneItems = ref([...])

useSortableList(todoRef, todoItems, {
  group: 'tasks',
  animation: 150,
})

useSortableList(doneRef, doneItems, {
  group: 'tasks',
  animation: 150,
})
</script>

<template>
  <div class="board">
    <div>
      <h3>할 일</h3>
      <ul ref="todoRef">
        <li v-for="item in todoItems" :key="item.id">{{ item.name }}</li>
      </ul>
    </div>
    <div>
      <h3>완료</h3>
      <ul ref="doneRef">
        <li v-for="item in doneItems" :key="item.id">{{ item.name }}</li>
      </ul>
    </div>
  </div>
</template>
```

### 3. 가로 정렬

```vue
<script setup lang="ts">
useSortableList(listRef, items, {
  direction: 'horizontal',
  animation: 200,
})
</script>

<template>
  <div ref="listRef" style="display: flex; gap: 10px;">
    <div v-for="item in items" :key="item.id" class="card">
      {{ item.name }}
    </div>
  </div>
</template>
```

---

## 성능 최적화

### 1. 큰 리스트 처리

```typescript
useSortableList(listRef, items, {
  animation: 0,             // 애니메이션 비활성화
  forceFallback: false,     // 네이티브 드래그 사용
})
```

### 2. 가상 스크롤과 함께 사용

가상 스크롤 라이브러리와 함께 사용할 때는 `draggable` 옵션으로 실제 DOM 요소만 선택합니다.

```typescript
useSortableList(listRef, items, {
  draggable: '.list-item',  // 실제 항목만 드래그 가능
})
```

---

## 스타일링

### Ghost 클래스

드래그 중인 항목의 반투명 복사본 스타일

```css
.sortable-ghost {
  opacity: 0.4;
  background: #f0f0f0;
}
```

### Drag 클래스

드래그되는 원본 항목 스타일

```css
.sortable-drag {
  opacity: 1;
  cursor: move;
}
```

### Chosen 클래스

선택된 항목 스타일

```css
.sortable-chosen {
  background: #e3f2fd;
}
```

### 커스텀 클래스 지정

```typescript
useSortableList(listRef, items, {
  ghostClass: 'my-ghost',
  dragClass: 'my-drag',
  chosenClass: 'my-chosen',
})
```

---

## 주의사항

### ✅ 장점

- **간편한 사용**: 기본 옵션으로 빠르게 시작
- **풍부한 기능**: Sortable.js의 모든 기능 활용 가능
- **반응성**: Vue의 ref와 완벽하게 통합
- **커스터마이징**: 다양한 옵션으로 세밀한 제어

### ⚠️ 제한사항

- **키 필수**: 각 항목은 고유한 `:key`가 필요
- **ref 타입**: HTMLElement ref만 지원
- **플러그인**: 일부 고급 기능은 Sortable.js 플러그인 필요

### 🚫 사용하지 말아야 할 경우

- 항목이 1개 이하인 경우
- 정렬이 필요 없는 정적 리스트
- 매우 큰 리스트 (1000개 이상) - 가상 스크롤 고려

---

## 트러블슈팅

### 드래그가 작동하지 않음

**원인**: ref가 제대로 연결되지 않음

**해결**:
1. `ref="listRef"` 확인
2. 컴포넌트 마운트 후 useSortable 호출 확인
3. 콘솔에서 `listRef.value` 확인

### 순서 변경이 반영되지 않음

**원인**: list가 reactive하지 않음

**해결**:
```typescript
// ❌ 잘못된 예
const items = [...]

// ✅ 올바른 예
const items = ref([...])
```

### 다른 이벤트와 충돌

**원인**: 드래그와 클릭 이벤트 충돌

**해결**:
```typescript
useSortableList(listRef, items, {
  delay: 200,              // 지연 시간 추가
  handle: '.drag-handle',  // 핸들 사용
})
```

### 그룹 간 이동 안 됨

**원인**: 그룹 이름 불일치 또는 put/pull 설정 문제

**해결**:
```typescript
// 모든 리스트에 같은 그룹 이름 사용
useSortableList(list1Ref, items1, { group: 'shared' })
useSortableList(list2Ref, items2, { group: 'shared' })
```

---

## 예제

자세한 예제는 아래 Stories를 참고하세요:

- **기본 사용**: 간단한 리스트 정렬
- **Handle 사용**: 드래그 핸들로 제어
- **Filter 사용**: 특정 항목 잠금
- **다중 리스트**: 여러 리스트 간 이동
- **가로 정렬**: 카드 형태 가로 정렬
- **이벤트 핸들러**: 이벤트 로깅

---

## 관련 리소스

- [Sortable.js 공식 문서](https://github.com/SortableJS/Sortable)
- [VueUse useSortable](https://vueuse.org/integrations/useSortable/)

---

## 버전 히스토리

- **v1.0.0** (2024-11): 초기 릴리스
  - useSortableList: 기본 옵션 포함 버전
  - useSortableCustom: 커스텀 옵션 버전
  - DEFAULT_SORTABLE_OPTIONS 상수 제공

