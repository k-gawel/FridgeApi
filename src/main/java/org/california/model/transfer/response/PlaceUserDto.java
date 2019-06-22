package org.california.model.transfer.response;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.california.model.entity.Account;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode @ToString
public class PlaceUserDto implements Serializable {

    private Long id;
    private String name;
    private Boolean status;
    private PlaceUserStats stats;
    

    public static class Builder {

        private PlaceUserDto result = new PlaceUserDto();

        public NameSetter setId(@NotNull Long id) {
            Builder.this.result.id = id;
            return new NameSetter();
        }

        public NameSetter setId(@NotNull Account account) {
            return setId(account.getId());
        }

        public class NameSetter {
            public StatusSetter setName(@NotEmpty String name) {
                Builder.this.result.name = name;
                return new StatusSetter();
            }

            public StatusSetter setName(@NotNull Account account) {
                return setName(account.getName());
            }
        }

        public class StatusSetter {
            public StatsSetter setStatus(boolean stauts) {
                Builder.this.result.status = stauts;
                return new StatsSetter();
            }
        }

        public class StatsSetter {
            public FinalBuilder setStats(PlaceUserStats stats) {
                Builder.this.result.stats = stats;
                return new FinalBuilder();
            }
        }

        public class FinalBuilder {
            public PlaceUserDto build() {
                return Builder.this.result;
            }
        }

    }

}
