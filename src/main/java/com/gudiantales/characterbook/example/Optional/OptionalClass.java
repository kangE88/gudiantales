package com.gudiantales.characterbook.example.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class OptionalClass {
    private Integer id;
    private String title;
    private boolean closed;

    public Progress progress;

    public OptionalClass(Integer id, String title, boolean closed) {
        this.id = id;
        this.title = title;
        this.closed = closed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    //return null 인것을 방지하기 위해 Optional
    //리턴값으로만 사용하길 권장.
    public Optional<Progress> getProgress() {
        return Optional.ofNullable(progress);
        /**
         * of : 무조건 널이 아닐때 사용
         */
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    // 매개변수로 사용할순있지만, Null을 세팅할수있기때문에 오히려 null체크 및 optional체크까지 해야하는 문제점이 발생한다.
    //        public void setProgress(Optional<Progress> progress) {
    //            progress.ifPresent((p) -> {  //isPresent() -> Null pointerException 발생
    //                this.progress = p;
    //            });
    //        }
}
