package com.linmt.openfeign;

import com.linmt.openfeign.annotations.EnableFeignClients;
import com.linmt.openfeign.annotations.FeignClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;
import java.util.function.Supplier;

@Configuration
public class FeignClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet();
        Set<String> basePackages = getBasePackage(metadata);

        for (String basePackage : basePackages) {
            ClassPathScanningCandidateComponentProvider scanner = this.createScanner();
            scanner.setResourceLoader(this.resourceLoader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));

            // 添加所有扫码到的类
            candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition beanDefinition : candidateComponents) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
                // 注册bean
                registerFeignClient(registry, annotationMetadata);
            }
        }
    }

    /**
     * 注册bean
     *
     * @param registry
     * @param annotationMetadata
     */
    private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata) {
        // 获取class名称
        String className = annotationMetadata.getClassName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        FeignClientFactoryBean factoryBean = new FeignClientFactoryBean(clazz);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, new Supplier() {
            @Override
            public Object get() {
                return factoryBean.getObject();
            }
        });
        registry.registerBeanDefinition(clazz.getName(), definition.getBeanDefinition());
    }

    /**
     * 创建类路径扫描器
     *
     * @return
     */
    private ClassPathScanningCandidateComponentProvider createScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                // isIndependent是顶级接口，isAnnotation是注解类
                if (beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation()) {
                    isCandidate = true;
                }

                return isCandidate;
            }
        };
    }


    /**
     * 获取
     *
     * @param metadata
     * @return
     */
    private Set<String> getBasePackage(AnnotationMetadata metadata) {
        Set<String> basePackage = new HashSet<>();

        // 获取启动类的包路径
        String bootstrapClassName = metadata.getClassName();
        String bootstrapClassPackage = bootstrapClassName.substring(0, bootstrapClassName.lastIndexOf("."));
        basePackage.add(bootstrapClassPackage);

        // 获取注解上面的包路径
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableFeignClients.class.getCanonicalName());
        String[] basePackages = (String[]) attributes.get("basePackages");
        basePackage.addAll(Arrays.asList(basePackages));

        return basePackage;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}