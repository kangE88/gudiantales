<route lang="yaml">
meta:
  id: input-field
  title: Input field
  menu: Module > Input field Module
  layout: EmptyLayout
  category: Module
  publish: 김대민
  publishVersion: 0.8
  navbar: false
</route>

<template>
  <div class="demo-title">0. UniversalInputField 사용 예시 (모든 props 사용 가능)</div>
  <!-- S : UniversalInputField 사용 예시 -->
  <div class="sc-input__field">
    <div class="input-field__group not-counter">
      <!-- 주민등록번호 - type으로 간단히 -->
      <div class="field-item">
        <UniversalInputField
          type="rrn"
          v-model="universalRrnValues"
          :input-field-props="{
            required: true,
            showClear: false,
          }"
        />
      </div>

      <!-- 휴대폰번호 - 모든 InputField props 사용 -->
      <div class="field-item">
        <UniversalInputField
          type="phone"
          v-model="universalPhoneValues"
          :input-field-props="{
            required: true,
            showClear: true,
            description: '본인 명의의 휴대폰번호를 입력해주세요',
          }"
          field-info="휴대폰번호는 필수 입력항목입니다."
        />
      </div>

      <!-- 카드번호 (4개 분할) - 에러 상태 -->
      <div class="field-item">
        <UniversalInputField
          type="card-number-split"
          v-model="universalCardValues"
          :input-field-props="{
            required: false,
            tooltip: '툴팁메시지',
            showClear: false,
            error: true,
            errorMessage: '카드번호를 확인해주세요',
            description: '안내메시지',
          }"
        />
      </div>

      <!-- 금액 입력 - 추가 슬롯 사용 -->
      <div class="field-item amount-field">
        <UniversalInputField
          type="amount"
          v-model="universalAmountValues"
          wrapper-class="amount-field"
          :input-field-props="{
            required: true,
            showClear: !!universalAmountValues.amount,
            description: '최소 10만원 / 최대 3,500만원',
          }"
        >
          <template #additional>
            <BoxButtonGroup
              variant="30:30:30"
              size="small"
            >
              <BoxButton
                text="+10만"
                color="tertiary"
                size="medium"
                @click="addUniversalAmount(100000)"
              />
              <BoxButton
                text="+50만"
                color="tertiary"
                size="medium"
                @click="addUniversalAmount(500000)"
              />
              <BoxButton
                text="+100만"
                color="tertiary"
                size="medium"
                @click="addUniversalAmount(1000000)"
              />
              <BoxButton
                text="+1,000만"
                color="tertiary"
                size="medium"
                @click="addUniversalAmount(10000000)"
              />
            </BoxButtonGroup>
          </template>
        </UniversalInputField>
      </div>
    </div>
  </div>
  <!-- E : UniversalInputField 사용 예시 -->

  <div class="demo-title">0-1. DropdownInputField 사용 예시 (Dropdown + InputField 조합)</div>
  <!-- S : DropdownInputField 사용 예시 -->
  <div class="sc-input__field">
    <div class="input-field__group not-counter">
      <!-- 통신사 + 휴대폰번호 -->
      <div class="field-item">
        <DropdownInputField
          type="phone-with-carrier"
          v-model="dropdownPhoneNumber"
          v-model:dropdown="dropdownCarrier"
          :options="carrierOptions"
          :input-field-props="{
            required: true,
            showClear: true,
          }"
          :dropdown-props="{
            placeholder: '통신사',
          }"
          :bottom-sheet-props="{
            title: '통신사를 선택해주세요',
          }"
          :auto-format="true"
          :validate-on-blur="true"
        />
      </div>

      <!-- 직업 + 상세 (커스텀 validator 사용) -->
      <div class="field-item">
        <DropdownInputField
          type="text"
          v-model="jobDetail"
          v-model:dropdown="selectedJob"
          :options="jobOptions"
          :input-field-props="{
            label: '직업 상세',
            placeholder: '상세 내용을 입력하세요',
            required: true,
          }"
          :dropdown-props="{
            placeholder: '직업 선택',
          }"
          :bottom-sheet-props="{
            title: '직업을 선택해주세요',
          }"
          :validator="(value) => (value.length < 2 ? '2자 이상 입력해주세요' : '')"
          :validate-on-blur="true"
        />
      </div>
    </div>
  </div>
  <!-- E : DropdownInputField 사용 예시 -->

  <div class="demo-title">1. 인풋 안내 정보가 없는 기본형</div>
  <!-- S : 1. 인풋 안내 정보가 없는 기본형 -->
  <div class="sc-input__field">
    <div class="input-field__group not-counter">
      <div class="field-item">
        <!-- 주민등록번호 -->
        <InputField
          label="주민등록번호"
          :required="true"
          :input-items="rrnInputItems"
          v-model:values="rrnValues"
          :show-clear="false"
        />
      </div>

      <div class="field-item">
        <!-- 주민등록번호 Error 예시 -->
        <InputField
          label="주민등록번호"
          :required="true"
          :input-items="rrnInputItems"
          v-model:values="rrnErrorValues"
          :show-clear="false"
          :error="true"
          error-message="에러 메시지"
          :length="0"
        />
      </div>
    </div>

    <div class="input-field__group not-counter">
      <!-- 카드 비밀번호 -->
      <div class="field-item">
        <InputField
          label="카드 비밀번호"
          :required="true"
          :input-items="cardPinInputItems"
          v-model:values="cardPinValues"
          :show-clear="true"
        />
      </div>

      <!-- 카드 비밀번호 Error 예시 -->
      <div class="field-item">
        <InputField
          label="카드 비밀번호"
          :required="true"
          :input-items="cardPinInputItems"
          v-model:values="cardPinErrorValues"
          :show-clear="true"
          :error="true"
          error-message="비밀번호 4자리를 입력해주세요"
        />
      </div>
    </div>
  </div>
  <!-- E : 1. 인풋 안내 정보가 없는 기본형 -->

  <div class="demo-title">2. 인풋 안내 정보가 있는 형태</div>
  <!-- S : 2. 인풋 안내 정보가 있는 형태 -->
  <div class="sc-input__field">
    <!-- 사업자등록번호 그룹 -->
    <div class="input-field__group not-counter">
      <!-- 사업자등록번호 -->
      <div class="field-item">
        <InputField
          label="사업자등록번호"
          :required="true"
          :input-items="businessNumberInputItems"
          v-model:values="businessNumberValues"
          :show-clear="true"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          사업자등록번호는 필수 입력항목입니다. 신한카드 가맹점주가 아니면 사업자등록증 사본을
          제출해주세요.
        </p>
      </div>

      <!-- 사업자등록번호 Error 예시 -->
      <div class="field-item">
        <InputField
          label="사업자등록번호"
          :required="true"
          :input-items="businessNumberInputItems"
          v-model:values="businessNumberErrorValues"
          :show-clear="true"
          :error="true"
          error-message="에러메세지"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          사업자등록번호는 필수 입력항목입니다. 신한카드 가맹점주가 아니면 사업자등록증 사본을
          제출해주세요.
        </p>
      </div>
    </div>

    <!-- 노동조합지부 그룹 -->
    <div class="input-field__group not-counter">
      <!-- 노동조합지부 -->
      <div class="field-item">
        <InputField
          label="노동조합지부"
          :required="true"
          :input-items="unionBranchInputItems"
          v-model:values="unionBranchValues"
          :show-clear="true"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          지역 대표전화 뒤의 2자리 숫자를 입력해주세요. 예) 서울: 02, 경기: 31, 인천: 32, 세종: 44
          등
        </p>
      </div>

      <!-- 노동조합지부 Error 예시 -->
      <div class="field-item">
        <InputField
          label="노동조합지부"
          :required="true"
          :input-items="unionBranchInputItems"
          v-model:values="unionBranchErrorValues"
          :show-clear="true"
          :error="true"
          error-message="에러메세지"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          지역 대표전화 뒤의 2자리 숫자를 입력해주세요. 예) 서울: 02, 경기: 31, 인천: 32, 세종: 44
          등
        </p>
      </div>
    </div>

    <!-- 휴대폰번호 그룹 -->
    <div class="input-field__group not-counter">
      <!-- 휴대폰번호 -->
      <div class="field-item">
        <InputField
          label="휴대폰번호"
          :required="true"
          :input-items="phoneNumberInputItems"
          v-model:values="phoneNumberValues"
          :show-clear="true"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          휴대폰번호는 필수 입력항목입니다. 본인 명의의 휴대폰번호를 입력해주세요.
        </p>
      </div>

      <!-- 휴대폰번호 Error 예시 -->
      <div class="field-item">
        <InputField
          label="휴대폰번호"
          :required="true"
          :input-items="phoneNumberInputItems"
          v-model:values="phoneNumberErrorValues"
          :show-clear="true"
          :error="true"
          error-message="에러메세지"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">
          휴대폰번호는 필수 입력항목입니다. 본인 명의의 휴대폰번호를 입력해주세요.
        </p>
      </div>
    </div>

    <!-- 영문성명 그룹 -->
    <div class="input-field__group not-counter">
      <!-- 영문성명 -->
      <div class="field-item">
        <InputField
          label="영문성"
          :required="true"
          :input-items="englishNameInputItems"
          v-model:values="englishNameValues"
          :show-clear="true"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">입력안내 문구 제공은 Item/Footer의 컬러가 유지됩니다.</p>
      </div>

      <!-- 영문성명 Error 예시 -->
      <div class="field-item">
        <InputField
          label="영문성"
          :required="true"
          :input-items="englishNameInputItems"
          v-model:values="englishNameErrorValues"
          :show-clear="true"
          :error="true"
          error-message="에러메세지"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">입력안내 문구 제공은 Item/Footer의 컬러가 유지됩니다.</p>
      </div>
    </div>
  </div>
  <!-- E : 2. 인풋 안내 정보가 있는 형태 -->

  <div class="demo-title">3. 안내정보와 오류 메세지가 동일한 경우</div>
  <!-- S : 3. 안내정보와 오류 메세지가 동일한 경우 -->
  <div class="sc-input__field">
    <div class="input-field__group">
      <!-- 레이블 필드 -->
      <div class="field-item">
        <InputField
          label="레이블"
          :required="true"
          :input-items="textFieldInputItems"
          v-model:values="textFieldValues"
          :show-clear="true"
          description="10자 이내로 입력하세요"
        />
      </div>

      <!-- 레이블 필드 Error 예시 -->
      <div class="field-item">
        <InputField
          label="레이블"
          :required="true"
          :input-items="textFieldInputItems"
          v-model:values="textFieldErrorValues"
          :show-clear="true"
          :error="true"
          error-message="10자 이내로 입력하세요"
        />
      </div>
    </div>
  </div>
  <!-- E : 3. 안내정보와 오류 메세지가 동일한 경우 -->

  <div class="demo-title">4. 안내정보와 오류 메세지가 동일하고 안내 정보가 같이 제공되는경우</div>
  <!-- S : 4. 안내정보와 오류 메세지가 동일하고 안내 정보가 같이 제공되는경우 -->
  <div class="sc-input__field">
    <div class="input-field__group not-counter">
      <!-- 레이블 필드 -->
      <div class="field-item">
        <InputField
          label="레이블"
          :required="true"
          :input-items="textField2InputItems"
          v-model:values="textField2Values"
          :show-clear="true"
          description="입력한 번호는 고객님 정보로 등록돼요."
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">입력안내 문구 제공은 Item/Footer의 컬러가 유지됩니다.</p>
      </div>

      <!-- 레이블 필드 Error 예시 -->
      <div class="field-item">
        <InputField
          label="레이블"
          :required="true"
          :input-items="textField2InputItems"
          v-model:values="textField2ErrorValues"
          :show-clear="true"
          :error="true"
          error-message="에러메세지"
        />
        <!-- field-info는 컴포넌트에 없는 유형으로 InputHelper 컴포넌트 보다 하단애 위치하는 구조 -->
        <p class="field-info">입력안내 문구 제공은 Item/Footer의 컬러가 유지됩니다.</p>
      </div>
    </div>
  </div>
  <!-- E : 4. 안내정보와 오류 메세지가 동일하고 안내 정보가 같이 제공되는경우 -->

  <div class="demo-title">5. 금액 단독입력 라인 인풋필드 (카드 특화)</div>
  <!-- S : 5. 금액 단독입력 라인 인풋필드 (카드 특화) -->
  <div class="sc-input__field">
    <div class="input-field__group not-counter">
      <!-- 금액 입력 필드 -->
      <div class="field-item amount-field">
        <InputField
          :required="true"
          :input-items="amountInputItems"
          v-model:values="amountValues"
          :show-clear="!!amountValues.amount"
          variant="underline"
          description="최소 10만원 / 최대 3,500만원"
        />

        <!-- 빠른 추가 버튼들 -->
        <BoxButtonGroup
          variant="30:30:30"
          size="small"
        >
          <BoxButton
            text="+10만"
            color="tertiary"
            size="medium"
            @click="addAmount(100000)"
          />
          <BoxButton
            text="+50만"
            color="tertiary"
            size="medium"
            @click="addAmount(500000)"
          />
          <BoxButton
            text="+100만"
            color="tertiary"
            size="medium"
            @click="addAmount(1000000)"
          />
          <BoxButton
            text="+1,000만"
            color="tertiary"
            size="medium"
            @click="addAmount(10000000)"
          />
        </BoxButtonGroup>
      </div>

      <!-- 금액 입력 필드 Error 예시 -->
      <div class="field-item amount-field">
        <InputField
          :required="true"
          :input-items="amountInputItems"
          v-model:values="amountErrorValues"
          :show-clear="!!amountErrorValues.amount"
          :error="true"
          error-message="최소 10만원 / 최대 3,500만원"
          variant="underline"
        />

        <!-- 빠른 추가 버튼들 -->
        <BoxButtonGroup
          variant="30:30:30"
          size="small"
        >
          <BoxButton
            text="+10만"
            color="tertiary"
            size="medium"
            @click="addAmountError(100000)"
          />
          <BoxButton
            text="+50만"
            color="tertiary"
            size="medium"
            @click="addAmountError(500000)"
          />
          <BoxButton
            text="+100만"
            color="tertiary"
            size="medium"
            @click="addAmountError(1000000)"
          />
          <BoxButton
            text="+1,000만"
            color="tertiary"
            size="medium"
            @click="addAmountError(10000000)"
          />
        </BoxButtonGroup>
      </div>

      <!-- 금액 입력 필드 Disabled 예시 -->
      <div class="field-item amount-field">
        <InputField
          :required="true"
          :input-items="amountInputItems"
          v-model:values="amountDisabledValues"
          :show-clear="false"
          :disabled="true"
          variant="underline"
          description="최소 10만원 / 최대 3,500만원"
        />

        <!-- 빠른 추가 버튼들 -->
        <BoxButtonGroup
          variant="30:30:30"
          size="small"
        >
          <BoxButton
            text="+10만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountDisabled(100000)"
          />
          <BoxButton
            text="+50만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountDisabled(500000)"
          />
          <BoxButton
            text="+100만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountDisabled(1000000)"
          />
          <BoxButton
            text="+1,000만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountDisabled(10000000)"
          />
        </BoxButtonGroup>
      </div>

      <!-- 금액 입력 필드 Readonly 예시 -->
      <div class="field-item amount-field">
        <InputField
          :required="true"
          :input-items="amountInputItems"
          v-model:values="amountReadonlyValues"
          :show-clear="false"
          :readonly="true"
          :disabled="true"
          variant="underline"
          description="최소 10만원 / 최대 3,500만원"
        />

        <!-- 빠른 추가 버튼들 -->
        <BoxButtonGroup
          variant="30:30:30"
          size="small"
        >
          <BoxButton
            text="+10만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountReadonly(100000)"
          />
          <BoxButton
            text="+50만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountReadonly(500000)"
          />
          <BoxButton
            text="+100만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountReadonly(1000000)"
          />
          <BoxButton
            text="+1,000만"
            color="tertiary"
            size="medium"
            :disabled="true"
            @click="addAmountReadonly(10000000)"
          />
        </BoxButtonGroup>
      </div>
    </div>
  </div>
  <!-- E : 5. 금액 단독입력 라인 인풋필드 (카드 특화) -->

  <div class="demo-title">6. 카드번호 입력 인풋</div>
  <!-- S : 6. 카드번호 입력 인풋 -->
  <div class="sc-input__field">
    <div class="input-field__group">
      <!-- 카드번호 입력 필드 (한줄 유형) -->
      <div class="field-item card-number-single-field">
        <UniversalInputField
          type="card-number-single"
          v-model="universalCardSingleValues"
          :input-field-props="{
            required: true,
            showClear: false,
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (한줄 유형 - 에러 상태) -->
      <div class="field-item card-number-single-field">
        <UniversalInputField
          type="card-number-single"
          v-model="universalCardSingleErrorValues"
          :input-field-props="{
            required: true,
            error: true,
            errorMessage: '안내메시지(문구는 추후 개발 전달 예정)',
            showClear: false,
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (한줄 유형 - 비활성화) -->
      <div class="field-item card-number-single-field">
        <UniversalInputField
          type="card-number-single"
          v-model="universalCardSingleDisabledValues"
          :input-field-props="{
            required: true,
            disabled: true,
            showClear: false,
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (한줄 유형 - 읽기전용) -->
      <div class="field-item card-number-single-field">
        <UniversalInputField
          type="card-number-single"
          v-model="universalCardSingleReadonlyValues"
          :input-field-props="{
            required: true,
            tooltip: '툴팁메시지',
            readonly: true,
            disabled: true,
            showClear: false,
          }"
        />
      </div>
    </div>
    <div class="input-field__group">
      <!-- 카드번호 입력 필드 (4개 분할) -->
      <div class="field-item">
        <UniversalInputField
          type="card-number-split"
          v-model="universalCardSplitValues"
          :input-field-props="{
            label: '레이블',
            required: false,
            tooltip: '툴팁메시지',
            showClear: false,
            description: '안내메시지',
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (4개 분할 - 에러 상태) -->
      <div class="field-item">
        <UniversalInputField
          type="card-number-split"
          v-model="universalCardSplitErrorValues"
          :input-field-props="{
            label: '레이블',
            required: false,
            tooltip: '툴팁메시지',
            error: true,
            errorMessage: '안내메시지(문구는 추후 개발 전달 예정)',
            showClear: false,
            description: '안내메시지',
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (4개 분할 - 비활성화) -->
      <div class="field-item">
        <UniversalInputField
          type="card-number-split"
          v-model="universalCardSplitDisabledValues"
          :input-field-props="{
            label: '레이블',
            required: false,
            tooltip: '툴팁메시지',
            disabled: true,
            showClear: false,
            description: '안내메시지',
          }"
        />
      </div>

      <!-- 카드번호 입력 필드 (4개 분할 - 읽기전용) -->
      <div class="field-item">
        <UniversalInputField
          type="card-number-split"
          v-model="universalCardSplitReadonlyValues"
          :input-field-props="{
            label: '레이블',
            required: false,
            tooltip: '툴팁메시지',
            readonly: true,
            disabled: true,
            showClear: false,
            description: '안내메시지',
          }"
        />
      </div>
    </div>
  </div>
  <!-- E : 6. 카드번호 입력 인풋 -->
</template>

<script setup>
import { BoxButton, BoxButtonGroup } from "@/components/Button";
import { InputField } from "@/components/InputField";
import { ref } from "vue";
import { DropdownInputField, UniversalInputField } from "~/components/shc/input";

// ============================================
// UniversalInputField 예시 데이터
// ============================================
const universalRrnValues = ref({});
const universalPhoneValues = ref({});
const universalCardValues = ref({});
const universalAmountValues = ref({});

// 금액 추가 함수
const addUniversalAmount = (value) => {
  const currentAmount = parseInt(universalAmountValues.value.amount?.replace(/[^0-9]/g, "") || "0");
  const newAmount = currentAmount + value;
  universalAmountValues.value = { amount: newAmount.toLocaleString() + "원" };
};

// ============================================
// DropdownInputField 예시 데이터
// ============================================
const dropdownPhoneNumber = ref("");
const dropdownCarrier = ref("");
const carrierOptions = ref([
  { label: "SKT", value: "SKT" },
  { label: "KT", value: "KT" },
  { label: "LG U+", value: "LG U+" },
  { label: "SKT 알뜰폰", value: "SKT 알뜰폰" },
  { label: "KT 알뜰폰", value: "KT 알뜰폰" },
  { label: "LG U+ 알뜰폰", value: "LG U+ 알뜰폰" },
]);

const jobDetail = ref("");
const selectedJob = ref("");
const jobOptions = ref([
  { label: "회사원", value: "office_worker" },
  { label: "자영업", value: "self_employed" },
  { label: "프리랜서", value: "freelancer" },
  { label: "학생", value: "student" },
  { label: "기타", value: "etc" },
]);

// 주민등록번호 듀얼 입력 (앞자리 + 뒷자리)
const rrnValues = ref({});
const rrnErrorValues = ref({});
const rrnInputItems = [
  {
    id: "rrn1",
    name: "rrn1",
    type: "tel",
    placeholder: "생년월일 6자리",
    mask: "######",
    length: 6,
    inputmode: "numeric",
  },
  {
    id: "rrn2",
    name: "rrn2",
    type: "tel",
    placeholder: "뒷자리 첫번째 숫자 입력",
    length: 1,
    inputmode: "numeric",
    mask: {
      mask: "nMMMMMM",
      overwrite: true,
      definitions: {
        n: { mask: "#", placeholderChar: "○" },
        m: { mask: "#", displayChar: "●", placeholderChar: "○" },
        M: { mask: "#", displayChar: "●", placeholderChar: "●" },
      },
      clearIncomplete: false,
      skipInvalid: true,
      lazy: false,
      eager: true,
    },
  },
];

// 숫자 입력 필드들 - inputItems 방식으로 변경
const cardPinValues = ref({});
const cardPinErrorValues = ref({});
const cardPinInputItems = [
  {
    id: "cardPin",
    name: "cardPin",
    type: "tel",
    label: "카드 비밀번호",
    placeholder: "카드 비밀번호 입력",
    length: 4,
    mask: {
      mask: "####",
      overwrite: false,
      definitions: {
        "#": { mask: "#", displayChar: "●" },
      },
    },
  },
];

const businessNumberValues = ref({});
const businessNumberErrorValues = ref({});
const businessNumberInputItems = [
  {
    id: "businessNumber",
    name: "businessNumber",
    type: "tel",
    label: "사업자등록번호",
    placeholder: "사업자등록번호 입력",
    length: 10,
  },
];

const phoneNumberValues = ref({});
const phoneNumberErrorValues = ref({});
const phoneNumberInputItems = [
  {
    id: "phoneNumber",
    name: "phoneNumber",
    type: "tel",
    label: "휴대폰번호",
    placeholder: "휴대폰번호 입력",
    length: 11,
    mask: {
      mask: "###-####-####",
      overwrite: false,
    },
  },
];

// 텍스트 입력 필드들 - inputItems 방식으로 변경
const unionBranchValues = ref({});
const unionBranchErrorValues = ref({});
const unionBranchInputItems = [
  {
    id: "unionBranch",
    name: "unionBranch",
    label: "노동조합지부",
    placeholder: "사업자등록번호 입력",
    length: 20,
    inputmode: "text",
  },
];

const englishNameValues = ref({});
const englishNameErrorValues = ref({});
const englishNameInputItems = [
  {
    id: "englishName",
    name: "englishName",
    label: "영문성명",
    placeholder: "영문이름",
    length: 50,
    inputmode: "latin",
    lang: "en-US",
  },
];

const textFieldValues = ref({});
const textFieldErrorValues = ref({});
const textFieldInputItems = [
  {
    id: "textField",
    name: "textField",
    label: "레이블",
    placeholder: "플레이스홀더",
    length: 10,
    inputmode: "text",
    lang: "ko",
  },
];

const textField2Values = ref({});
const textField2ErrorValues = ref({});
const textField2InputItems = [
  {
    id: "textField2",
    name: "textField2",
    label: "레이블",
    placeholder: "플레이스홀더",
    length: 10,
    inputmode: "text",
    lang: "ko",
  },
];

// 금액 입력 필드들 - 카드 특화
const amountValues = ref({});
const amountErrorValues = ref({});
const amountDisabledValues = ref({});
const amountReadonlyValues = ref({ amount: "3,500만원" });
const amountInputItems = [
  {
    id: "amount",
    name: "amount",
    type: "tel",
    label: "금액",
    placeholder: "1만원 단위로 입력",
    length: 15,
    inputmode: "numeric",
  },
];

// ============================================
// UniversalInputField 카드번호 예시 데이터
// ============================================
const universalCardSingleValues = ref({});
const universalCardSingleErrorValues = ref({});
const universalCardSingleDisabledValues = ref({});
const universalCardSingleReadonlyValues = ref({
  cardNumber: "1234-5678-●●●●-123●",
});

const universalCardSplitValues = ref({});
const universalCardSplitErrorValues = ref({});
const universalCardSplitDisabledValues = ref({});
const universalCardSplitReadonlyValues = ref({
  cardNumber1: "1234",
  cardNumber2: "5678",
  cardNumber3: "●●●●",
  cardNumber4: "●●●●",
});
</script>

<style lang="scss" scoped>
@use "_input-field" as *; // Input field 모듈
</style>
