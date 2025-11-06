<route lang="yaml">
meta:
  id: useSortableEx
  title: useSortableEx 테스트
  menu: useSortableEx
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

<script setup>
import { useSortableCustom, useSortableList } from '@shc-nss/shared'
import {
  BottomActionContainer,
  BoxButton,
  BoxButtonGroup,
  IconButton,
  ListItem,
  SelectBoxGroup
} from '@shc-nss/ui/solid'
import { ref, watch } from 'vue'

// 데모용 이미지
import imgSample1 from '@assets/images/pages/demo/img-sample1.png'
import imgSample2 from '@assets/images/pages/demo/img-sample2.png'

// 현재 선택된 탭
const activeTab = ref(0)

// 이벤트 로그
const eventLog = ref([])
const addLog = (message) => {
  eventLog.value.unshift(`[${new Date().toLocaleTimeString()}] ${message}`)
  if (eventLog.value.length > 10) {
    eventLog.value.pop()
  }
}
const clearLog = () => {
  eventLog.value = []
}

// ============================================
// 1. 기본 예제 (useSortableList)
// ============================================
const basicEl = ref()
const basicPickIndex = ref()
const basicList = ref([
  { label: '항목 1', value: 'i1', main: '신한 Deep Dream 1', sub: '기본 정렬 예제', image: imgSample1 },
  { label: '항목 2', value: 'i2', main: '신한 Deep Dream 2', sub: '애니메이션 150ms', image: imgSample2 },
  { label: '항목 3', value: 'i3', main: '신한 Deep Dream 3', sub: '스크롤 감도 50px', image: imgSample1 },
  { label: '항목 4', value: 'i4', main: '신한 Deep Dream 4', sub: '스크롤 속도 20', image: imgSample2 },
  { label: '항목 5', value: 'i5', main: '신한 Deep Dream 5', sub: '드래그하여 순서 변경', image: imgSample1 },
])

useSortableList(basicEl, basicList, {
  onStart: (evt) => {
    addLog(`[기본] 드래그 시작: ${evt.oldIndex + 1}번 항목`)
  },
  onEnd: (evt) => {
    addLog(`[기본] 드래그 종료: ${evt.oldIndex + 1}번 → ${evt.newIndex + 1}번`)
  },
})

// ============================================
// 2. Handle 옵션 - 특정 핸들로만 드래그
// ============================================
const handleEl = ref()
const handlePickIndex = ref()
const handleList = ref([
  { label: '항목 A', value: 'h1', main: '항목 A', sub: '핸들 아이콘(☰)을 잡고 드래그하세요', image: imgSample1 },
  { label: '항목 B', value: 'h2', main: '항목 B', sub: '다른 영역은 드래그 불가', image: imgSample2 },
  { label: '항목 C', value: 'h3', main: '항목 C', sub: '핸들만 활성화', image: imgSample1 },
  { label: '항목 D', value: 'h4', main: '항목 D', sub: '정확한 제어 가능', image: imgSample2 },
])

useSortableCustom(handleEl, handleList, {
  animation: 200,
  handle: '.drag-handle',
  onUpdate: (evt) => {
    addLog(`[핸들] 위치 변경: ${evt.oldIndex + 1}번 → ${evt.newIndex + 1}번`)
  },
})

// ============================================
// 3. Delay 옵션 - 드래그 시작 지연
// ============================================
const delayEl = ref()
const delayPickIndex = ref()
const delayList = ref([
  { label: '지연 1', value: 'd1', main: '지연 항목 1', sub: '300ms 후 드래그 시작', image: imgSample1 },
  { label: '지연 2', value: 'd2', main: '지연 항목 2', sub: '실수 방지에 유용', image: imgSample2 },
  { label: '지연 3', value: 'd3', main: '지연 항목 3', sub: '모바일 환경 권장', image: imgSample1 },
])

useSortableCustom(delayEl, delayList, {
  animation: 150,
  delay: 300,
  onStart: () => {
    addLog('[지연] 300ms 지연 후 드래그 시작됨')
  },
})

// ============================================
// 4. Disabled 옵션 - 동적 활성화/비활성화
// ============================================
const disabledEl = ref()
const disabledPickIndex = ref()
const isDisabled = ref(false)
const disabledList = ref([
  { label: '토글 1', value: 'dis1', main: '토글 항목 1', sub: '정렬 활성화/비활성화', image: imgSample1 },
  { label: '토글 2', value: 'dis2', main: '토글 항목 2', sub: '체크박스로 제어', image: imgSample2 },
  { label: '토글 3', value: 'dis3', main: '토글 항목 3', sub: '동적 제어 가능', image: imgSample1 },
])

useSortableCustom(disabledEl, disabledList, {
  animation: 150,
  get disabled() {
    return isDisabled.value
  },
  onStart: () => {
    addLog('[토글] 정렬 가능 - 드래그 시작')
  },
})

watch(isDisabled, (newValue) => {
  addLog(`[토글] 정렬 ${newValue ? '비활성화' : '활성화'}`)
})

// ============================================
// 5. Filter 옵션 - 특정 항목 드래그 방지
// ============================================
const filterEl = ref()
const filterPickIndex = ref()
const filterList = ref([
  { label: '일반 1', value: 'f1', main: '일반 항목 1', sub: '드래그 가능', image: imgSample1, locked: false },
  { label: '잠금', value: 'f2', main: '🔒 잠금 항목', sub: '드래그 불가', image: imgSample2, locked: true },
  { label: '일반 2', value: 'f3', main: '일반 항목 2', sub: '드래그 가능', image: imgSample1, locked: false },
  { label: '잠금', value: 'f4', main: '🔒 잠금 항목', sub: '드래그 불가', image: imgSample2, locked: true },
  { label: '일반 3', value: 'f5', main: '일반 항목 3', sub: '드래그 가능', image: imgSample1, locked: false },
])

useSortableCustom(filterEl, filterList, {
  animation: 150,
  filter: '.locked-item',
  onFilter: (evt) => {
    addLog('[필터] 잠긴 항목은 드래그할 수 없습니다')
  },
})

// ============================================
// 6. Group 옵션 - 여러 리스트 간 이동
// ============================================
const groupEl1 = ref()
const groupEl2 = ref()
const groupPickIndex1 = ref()
const groupPickIndex2 = ref()
const groupList1 = ref([
  { label: '그룹A-1', value: 'g1', main: '그룹A 항목 1', sub: '다른 그룹으로 이동 가능', image: imgSample1 },
  { label: '그룹A-2', value: 'g2', main: '그룹A 항목 2', sub: '드래그하여 이동', image: imgSample2 },
  { label: '그룹A-3', value: 'g3', main: '그룹A 항목 3', sub: '그룹 간 공유', image: imgSample1 },
])
const groupList2 = ref([
  { label: '그룹B-1', value: 'g4', main: '그룹B 항목 1', sub: '다른 그룹으로 이동 가능', image: imgSample2 },
  { label: '그룹B-2', value: 'g5', main: '그룹B 항목 2', sub: '드래그하여 이동', image: imgSample1 },
  { label: '그룹B-3', value: 'g6', main: '그룹B 항목 3', sub: '그룹 간 공유', image: imgSample2 },
])

useSortableCustom(groupEl1, groupList1, {
  animation: 150,
  group: 'shared',
  onAdd: (evt) => {
    addLog(`[그룹A] 항목 추가됨: ${evt.newIndex + 1}번 위치`)
  },
  onRemove: (evt) => {
    addLog(`[그룹A] 항목 제거됨: ${evt.oldIndex + 1}번 위치`)
  },
})

useSortableCustom(groupEl2, groupList2, {
  animation: 150,
  group: 'shared',
  onAdd: (evt) => {
    addLog(`[그룹B] 항목 추가됨: ${evt.newIndex + 1}번 위치`)
  },
  onRemove: (evt) => {
    addLog(`[그룹B] 항목 제거됨: ${evt.oldIndex + 1}번 위치`)
  },
})

// 선택된 항목 클릭 핸들러
const onClickItem = (item, pickIndexRef) => {
  pickIndexRef.value = item.value
}
</script>

<template>
  <div class="sortable-examples">
    <!-- 이벤트 로그 -->
    <section class="log-section">
      <div class="log-header">
        <h3>📋 이벤트 로그</h3>
        <BoxButton text="초기화" size="small" @click="clearLog" />
      </div>
      <div class="log-content">
        <div v-if="eventLog.length === 0" class="log-empty">
          이벤트가 없습니다. 항목을 드래그해보세요!
        </div>
        <div v-for="(log, index) in eventLog" :key="index" class="log-item">
          {{ log }}
        </div>
      </div>
    </section>

    <!-- 탭 네비게이션 -->
    <div class="tab-navigation">
      <button
        v-for="(tab, index) in ['기본', 'Handle', 'Delay', 'Disabled', 'Filter', 'Group']"
        :key="index"
        :class="['tab-button', { active: activeTab === index }]"
        @click="activeTab = index"
      >
        {{ tab }}
      </button>
    </div>

    <!-- 탭 컨텐츠 -->
    <div class="tab-content">
      <!-- 1. 기본 예제 -->
      <section v-show="activeTab === 0" class="example-section">
        <h2>1️⃣ 기본 옵션 (useSortableList)</h2>
        <p class="description">
          • animation: 150ms - 애니메이션 속도<br />
          • scrollSensitivity: 50px - 스크롤 시작 감도<br />
          • scrollSpeed: 20 - 스크롤 속도<br />
          • onStart, onEnd - 이벤트 핸들러<br />
          💡 오른쪽 메뉴 아이콘을 드래그하세요
        </p>
        <div class="sc-list sc-select__list">
          <div class="select-list__group select-list__image">
            <SelectBoxGroup
              v-model="basicPickIndex"
              orientation="vertical"
              variant="solid"
              as="div"
              :items="basicList"
              ref="basicEl"
            >
              <template #contents="{ item }">
                <ListItem :left="{ mainText: item.main, subText: item.sub }">
                  <template #leftIcon>
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt=""
                      class="thumb"
                      @click="onClickItem(item, basicPickIndex)"
                    />
                  </template>
                  <template #rightIcon>
                    <IconButton
                      iconName="Menu"
                      size="medium"
                      aria-label="드래그하여 순서 변경"
                      @click.stop
                    />
                  </template>
                </ListItem>
              </template>
            </SelectBoxGroup>
          </div>
        </div>
      </section>

      <!-- 2. Handle 옵션 -->
      <section v-show="activeTab === 1" class="example-section">
        <h2>2️⃣ Handle 옵션</h2>
        <p class="description">
          • handle: '.drag-handle' - 특정 요소를 잡아야만 드래그 가능<br />
          • onUpdate - 정렬 업데이트 이벤트<br />
          💡 메뉴 아이콘만 드래그 가능합니다 (다른 영역 클릭은 선택 동작)
        </p>
        <div class="sc-list sc-select__list">
          <div class="select-list__group select-list__image">
            <SelectBoxGroup
              v-model="handlePickIndex"
              orientation="vertical"
              variant="solid"
              as="div"
              :items="handleList"
              ref="handleEl"
            >
              <template #contents="{ item }">
                <ListItem :left="{ mainText: item.main, subText: item.sub }">
                  <template #leftIcon>
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt=""
                      class="thumb"
                      @click="onClickItem(item, handlePickIndex)"
                    />
                  </template>
                  <template #rightIcon>
                    <div class="drag-handle" style="cursor: grab; padding: 8px;">
                      <IconButton
                        iconName="Menu"
                        size="medium"
                        aria-label="드래그하여 순서 변경"
                        @click.stop
                      />
                    </div>
                  </template>
                </ListItem>
              </template>
            </SelectBoxGroup>
          </div>
        </div>
      </section>

      <!-- 3. Delay 옵션 -->
      <section v-show="activeTab === 2" class="example-section">
        <h2>3️⃣ Delay 옵션</h2>
        <p class="description">
          • delay: 300ms - 마우스를 누르고 300ms 후에 드래그 시작<br />
          • 실수로 인한 드래그 방지 (모바일 환경에 유용)<br />
          💡 누르고 있으면 300ms 후 드래그가 시작됩니다
        </p>
        <div class="sc-list sc-select__list">
          <div class="select-list__group select-list__image">
            <SelectBoxGroup
              v-model="delayPickIndex"
              orientation="vertical"
              variant="solid"
              as="div"
              :items="delayList"
              ref="delayEl"
            >
              <template #contents="{ item }">
                <ListItem :left="{ mainText: item.main, subText: item.sub }">
                  <template #leftIcon>
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt=""
                      class="thumb"
                      @click="onClickItem(item, delayPickIndex)"
                    />
                  </template>
                  <template #rightIcon>
                    <IconButton
                      iconName="Menu"
                      size="medium"
                      aria-label="드래그하여 순서 변경"
                      @click.stop
                    />
                  </template>
                </ListItem>
              </template>
            </SelectBoxGroup>
          </div>
        </div>
      </section>

      <!-- 4. Disabled 옵션 -->
      <section v-show="activeTab === 3" class="example-section">
        <h2>4️⃣ Disabled 옵션</h2>
        <p class="description">
          • disabled: boolean - 동적으로 정렬 기능 제어<br />
          💡 아래 체크박스로 정렬 기능을 켜고 끌 수 있습니다
        </p>
        <div class="checkbox-container">
          <label class="checkbox-label">
            <input type="checkbox" v-model="isDisabled" />
            정렬 비활성화 (현재: {{ isDisabled ? '비활성' : '활성' }})
          </label>
        </div>
        <div class="sc-list sc-select__list" :class="{ disabled: isDisabled }">
          <div class="select-list__group select-list__image">
            <SelectBoxGroup
              v-model="disabledPickIndex"
              orientation="vertical"
              variant="solid"
              as="div"
              :items="disabledList"
              ref="disabledEl"
            >
              <template #contents="{ item }">
                <ListItem :left="{ mainText: item.main, subText: item.sub }">
                  <template #leftIcon>
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt=""
                      class="thumb"
                      @click="onClickItem(item, disabledPickIndex)"
                    />
                  </template>
                  <template #rightIcon>
                    <IconButton
                      iconName="Menu"
                      size="medium"
                      aria-label="드래그하여 순서 변경"
                      @click.stop
                    />
                  </template>
                </ListItem>
              </template>
            </SelectBoxGroup>
          </div>
        </div>
      </section>

      <!-- 5. Filter 옵션 -->
      <section v-show="activeTab === 4" class="example-section">
        <h2>5️⃣ Filter 옵션</h2>
        <p class="description">
          • filter: '.locked-item' - 특정 클래스를 가진 항목은 드래그 불가<br />
          • onFilter - 필터링된 항목 클릭 시 이벤트<br />
          💡 🔒 잠금 표시가 있는 항목은 드래그할 수 없습니다
        </p>
        <div class="sc-list sc-select__list">
          <div class="select-list__group select-list__image">
            <SelectBoxGroup
              v-model="filterPickIndex"
              orientation="vertical"
              variant="solid"
              as="div"
              :items="filterList"
              ref="filterEl"
            >
              <template #contents="{ item }">
                <ListItem
                  :left="{ mainText: item.main, subText: item.sub }"
                  :class="{ 'locked-item': item.locked }"
                >
                  <template #leftIcon>
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt=""
                      class="thumb"
                      :style="{ opacity: item.locked ? 0.5 : 1 }"
                      @click="onClickItem(item, filterPickIndex)"
                    />
                  </template>
                  <template #rightIcon>
                    <IconButton
                      v-if="!item.locked"
                      iconName="Menu"
                      size="medium"
                      aria-label="드래그하여 순서 변경"
                      @click.stop
                    />
                    <IconButton
                      v-else
                      iconName="Lock"
                      size="medium"
                      aria-label="잠금"
                      @click.stop
                    />
                  </template>
                </ListItem>
              </template>
            </SelectBoxGroup>
          </div>
        </div>
      </section>

      <!-- 6. Group 옵션 -->
      <section v-show="activeTab === 5" class="example-section">
        <h2>6️⃣ Group 옵션 - 여러 리스트 간 이동</h2>
        <p class="description">
          • group: 'shared' - 같은 그룹끼리 항목 이동 가능<br />
          • onAdd, onRemove - 항목 추가/제거 이벤트<br />
          💡 두 그룹 사이에서 항목을 드래그하여 이동할 수 있습니다
        </p>
        <div class="group-container">
          <div class="group-box">
            <h4>그룹 A ({{ groupList1.length }}개)</h4>
            <div class="sc-list sc-select__list">
              <div class="select-list__group select-list__image">
                <SelectBoxGroup
                  v-model="groupPickIndex1"
                  orientation="vertical"
                  variant="solid"
                  as="div"
                  :items="groupList1"
                  ref="groupEl1"
                >
                  <template #contents="{ item }">
                    <ListItem :left="{ mainText: item.main, subText: item.sub }">
                      <template #leftIcon>
                        <img
                          v-if="item.image"
                          :src="item.image"
                          alt=""
                          class="thumb"
                          @click="onClickItem(item, groupPickIndex1)"
                        />
                      </template>
                      <template #rightIcon>
                        <IconButton
                          iconName="Menu"
                          size="medium"
                          aria-label="드래그하여 순서 변경"
                          @click.stop
                        />
                      </template>
                    </ListItem>
                  </template>
                </SelectBoxGroup>
              </div>
            </div>
          </div>

          <div class="group-box">
            <h4>그룹 B ({{ groupList2.length }}개)</h4>
            <div class="sc-list sc-select__list">
              <div class="select-list__group select-list__image">
                <SelectBoxGroup
                  v-model="groupPickIndex2"
                  orientation="vertical"
                  variant="solid"
                  as="div"
                  :items="groupList2"
                  ref="groupEl2"
                >
                  <template #contents="{ item }">
                    <ListItem :left="{ mainText: item.main, subText: item.sub }">
                      <template #leftIcon>
                        <img
                          v-if="item.image"
                          :src="item.image"
                          alt=""
                          class="thumb"
                          @click="onClickItem(item, groupPickIndex2)"
                        />
                      </template>
                      <template #rightIcon>
                        <IconButton
                          iconName="Menu"
                          size="medium"
                          aria-label="드래그하여 순서 변경"
                          @click.stop
                        />
                      </template>
                    </ListItem>
                  </template>
                </SelectBoxGroup>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>

  <BottomActionContainer :scrollDim="true">
    <BoxButtonGroup size="xlarge">
      <BoxButton text="완료" @click="addLog('완료 버튼 클릭')" />
    </BoxButtonGroup>
  </BottomActionContainer>
</template>

<style lang="scss" scoped>
.sortable-examples {
  padding: 20px;
  padding-bottom: 100px;
}

// 이벤트 로그
.log-section {
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;

  .log-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;

    h3 {
      margin: 0;
      font-size: 1.1rem;
      font-weight: 600;
    }
  }

  .log-content {
    max-height: 150px;
    overflow-y: auto;
    background: white;
    border-radius: 4px;
    padding: 10px;

    .log-empty {
      color: #6c757d;
      font-style: italic;
      text-align: center;
      padding: 20px;
      font-size: 0.9rem;
    }

    .log-item {
      padding: 6px 8px;
      border-bottom: 1px solid #e9ecef;
      font-size: 0.85rem;
      color: #495057;
      font-family: monospace;

      &:last-child {
        border-bottom: none;
      }
    }
  }
}

// 탭 네비게이션
.tab-navigation {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 5px;

  .tab-button {
    padding: 10px 20px;
    background: white;
    border: 2px solid #dee2e6;
    border-radius: 8px;
    cursor: pointer;
    font-size: 0.9rem;
    font-weight: 500;
    transition: all 0.2s;
    white-space: nowrap;

    &:hover {
      border-color: #007bff;
      background: #f8f9fa;
    }

    &.active {
      background: #007bff;
      color: white;
      border-color: #007bff;
    }
  }
}

// 예제 섹션
.example-section {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;

  h2 {
    font-size: 1.3rem;
    margin: 0 0 12px 0;
    color: #333;
  }

  .description {
    color: #6c757d;
    font-size: 0.9rem;
    line-height: 1.6;
    margin-bottom: 20px;
    background: #f8f9fa;
    padding: 12px;
    border-radius: 4px;
    border-left: 4px solid #007bff;
  }
}

// 체크박스
.checkbox-container {
  margin-bottom: 15px;

  .checkbox-label {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    font-weight: 600;
    color: #495057;
    font-size: 0.95rem;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
      cursor: pointer;
    }
  }
}

// 비활성화 상태
.sc-list.disabled {
  opacity: 0.5;
  pointer-events: none;
}

// 잠긴 항목
.locked-item {
  opacity: 0.7;
  cursor: not-allowed !important;
}

// 그룹 컨테이너
.group-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }

  .group-box {
    border: 2px solid #dee2e6;
    border-radius: 8px;
    overflow: hidden;

    h4 {
      margin: 0;
      padding: 12px;
      background: #007bff;
      color: white;
      text-align: center;
      font-size: 1rem;
    }

    .sc-list {
      min-height: 200px;
    }
  }
}

// 드래그 핸들 스타일
.drag-handle {
  &:active {
    cursor: grabbing !important;
  }
}

// 드래그 중 스타일
:deep(.sortable-ghost) {
  opacity: 0.4;
}

:deep(.sortable-drag) {
  opacity: 0.9;
}
</style>
