package javier.com.mydorm1.auth.init;

import jakarta.annotation.PostConstruct;
import javier.com.mydorm1.auth.annotation.View;
import javier.com.mydorm1.auth.model.Permission;
import javier.com.mydorm1.auth.model.Role;
import javier.com.mydorm1.auth.model.User;
import javier.com.mydorm1.auth.repo.PermissionRepository;
import javier.com.mydorm1.auth.repo.RoleRepository;
import javier.com.mydorm1.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javier.com.mydorm1.auth.model.Status.ACTIVE;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnnotationInitialScan {
    @Value("${initial.role-admin.code}")
    String roleAdminCode;
    @Value("${initial.role-user.code}")
    String roleUserCode;
    @Value("${initial.role-captain.code}")
    String roleCaptainCode;
    @Value("${initial.admin.username}")
    String adminUsername;
    @Value("${initial.user.username}")
    String userUsername;
    @Value("${initial.admin.password}")
    String adminPassword;
    @Value("${initial.user.password}")
    String userPassword;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void postConstruct() {
        startView();
        startPerm();
        Role adminRole = createRole(roleAdminCode,TRUE);
        Role userRole = createRole(roleUserCode,FALSE);
        Role roleCaptain = createRole(roleCaptainCode, FALSE);
        createUser(adminUsername,adminRole,adminPassword);
        createUser(userUsername,userRole,userPassword);
    }

    public void startView() {
        log.info("---------------------------------------");
        log.info("-------Scan for View annotations-------");
        log.info("---------------------------------------");
        Reflections reflections = new Reflections("project.controller", new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(View.class);
        methods.forEach(method -> {
            View perm = method.getAnnotation(View.class);
            for (String code : perm.value()) {
                try {
                    String methodType = method.getDeclaringClass().getSimpleName();
                    String type = null;
                    if (methodType.contains("Controller"))
                        type = methodType.replace("Controller", "");
                    if (type != null) {
                        Permission permissionOpt = permissionRepository.getPermissionByName(code);
                        if (permissionOpt == null) {
                            Permission permission = new Permission();
                            permission.setName(code);
                            permission.setClassName(type);
                            permissionRepository.save(permission);
                            log.info("Found a permission '" + code + "'");
                        }
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    public void startPerm() {
        log.info("---------------------------------------");
        log.info("----Scan for Permission annotations----");
        log.info("---------------------------------------");
        Reflections reflections = new Reflections(
                "javier.com.mydorm1", new MethodAnnotationsScanner()
        );
        List<Permission> permissions = new ArrayList<>();
        List<String> permissionNames = new ArrayList<>();
        Set<Method> methods = reflections.getMethodsAnnotatedWith(PreAuthorize.class);
        methods.forEach(method -> {
            PreAuthorize perm = method.getAnnotation(PreAuthorize.class);
            String name = perm.value();
            if (name.contains("hasAuthority")) {
                String codeAndName = name.replace("hasAuthority('", "").replace("')", "");
                try {
                    String methodType = method.getDeclaringClass().getSimpleName();
                    String type = null;
                    if (methodType.contains("Controller"))
                        type = methodType.replace("Controller", "");
                    if (type != null) {
                        if (!permissionRepository.existByName(codeAndName) && !permissionNames.contains(codeAndName)) {
                            Permission permission = new Permission();
                            permission.setName(codeAndName);
                            if (codeAndName.equals("View Reference")) {
                                permission.setClassName("References");
                            } else {
                                permission.setClassName(type);
                            }
                            permissions.add(permission);
                            permissionNames.add(codeAndName);
                        }
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            } else if (name.contains("hasAnyAuthority")) {
                String codeAndName = name.replace("hasAnyAuthority('", "").replace("')", "");
                String[] codeAndNames = codeAndName.split(",");
                try {
                    String methodType = method.getDeclaringClass().getSimpleName();
                    String type = null;
                    if (methodType.contains("Controller"))
                        type = methodType.replace("Controller", "");
                    if (type != null) {
                        for (String andName : codeAndNames) {
                            String permissionName = andName.replaceAll("'", "").trim();
                            if (!permissionRepository.existByName(permissionName) && !permissionNames.contains(andName)) {
                                Permission permission = new Permission();
                                permission.setName(permissionName);
                                if (permissionName.equals("View Reference")) {
                                    permission.setClassName("References");
                                } else {
                                    permission.setClassName(type);
                                }
                                permissions.add(permission);
                                permissionNames.add(permissionName);
                            }
                        }
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        permissionRepository.saveAll(permissions);
    }

    public Role createRole(String roleCode,Boolean isAdmin) {
        Role role = roleRepository.findByCodeEager(roleCode);
        if (role == null) {
            role = new Role();
            role.setName(roleCode);
            role.setCode(roleCode);
        }
        if (isAdmin) {
            role.setPermissions(new HashSet<>(permissionRepository.findAll()));
        }
        return roleRepository.save(role);
    }

    public void createUser(String userName,Role role,String password) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            user = new User();
            user.setUsername(userName);
            user.setStatus(ACTIVE);
            user.setEnabled(TRUE);
            user.setPassword(passwordEncoder.encode(password));
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }
}
