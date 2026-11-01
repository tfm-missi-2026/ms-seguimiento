package pe.unir.tfm.srp.seguimiento;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("pe.unir.tfm.srp.seguimiento.repository")
public class MsSeguimientoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSeguimientoApplication.class, args);
    }
}
