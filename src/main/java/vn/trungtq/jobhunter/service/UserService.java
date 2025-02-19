package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Company;
import vn.trungtq.jobhunter.domain.Role;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.response.ResCreateUserDTO;
import vn.trungtq.jobhunter.domain.response.ResUpdateUserDTO;
import vn.trungtq.jobhunter.domain.response.ResUserDTO;
import vn.trungtq.jobhunter.repository.CompanyRepository;
import vn.trungtq.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository,
                       CompanyRepository companyRepository,
                       RoleService roleService
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.orElse(null));
        }
        //check role
        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRole(user.getRole().getId());
            user.setRole(role !=null ? role : null);
        }
         return this.userRepository.save(user);
    }
    public void handleDeleteUser(long id) {
         this.userRepository.deleteById(id);
    }
    public User handleGetUser(long id) {
        return this.userRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = userRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageUsers.getTotalElements());
        meta.setPages(pageUsers.getTotalPages());
        rs.setMeta(meta);
        List<ResUserDTO> listUser = pageUsers.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }
    public User handleUpdateUser(User user) {
        User curUser = handleGetUser(user.getId());
        if (curUser != null) {
            curUser.setName(user.getName());
            curUser.setAddress(user.getAddress());
            curUser.setAge(user.getAge());
            curUser.setGender(user.getGender());
            if(user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
                curUser.setCompany(companyOptional.orElse(null));
            }

            //check company
            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
                curUser.setCompany(companyOptional.orElse(null));
            }
            //check role
            if (user.getRole() != null) {
                Role role = this.roleService.handleGetRole(user.getRole().getId());
                curUser.setRole(role !=null ? role : null);
            }

            curUser = this.userRepository.save(curUser);

        }
        return  curUser;
    }
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
    public boolean checkEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO rs = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();

        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreatedBy());
        rs.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            rs.setCompany(company);
        }
        return rs;
    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO rs = new ResUserDTO();
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            rs.setCompany(company);
        }
        if (user.getRole() != null){
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            rs.setRole(roleUser);
        }
        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreatedBy());
        rs.setUpdateBy(user.getUpdatedBy());
        rs.setCreatedAt(user.getCreatedAt());
        rs.setUpdatedAt(user.getUpdatedAt());
        return rs;
    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO rs = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser();

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            rs.setCompany(company);
        }
        rs.setId(user.getId());
        rs.setName(user.getName());
        rs.setEmail(user.getEmail());
        rs.setAge(user.getAge());
        rs.setAddress(user.getAddress());
        rs.setGender(user.getGender());
        rs.setCreateBy(user.getCreatedBy());
        rs.setUpdateBy(user.getUpdatedBy());
        rs.setCreatedAt(user.getCreatedAt());
        rs.setUpdatedAt(user.getUpdatedAt());
        return rs;
    }
    public void updateUserToken(String refreshToken,String email){
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken,String email){
       return this.userRepository.findByRefreshTokenAndEmail(refreshToken,email);
    }
}
