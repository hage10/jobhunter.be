package vn.trungtq.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.enums.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private Instant updatedAt;
    private String CreateBy;
    private String UpdateBy;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

}
