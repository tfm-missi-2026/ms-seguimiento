package pe.unir.tfm.srp.seguimiento.config;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update",
               args = {MappedStatement.class, Object.class})
})
public class AuditoriaInterceptor implements Interceptor {

    private final CurrentUserResolver currentUserResolver;

    @Autowired
    public AuditoriaInterceptor(@Lazy CurrentUserResolver currentUserResolver) {
        this.currentUserResolver = currentUserResolver;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parametro = invocation.getArgs()[1];
        SqlCommandType tipo = ms.getSqlCommandType();

        if (parametro != null && (tipo == SqlCommandType.INSERT || tipo == SqlCommandType.UPDATE)) {
            poblarAuditoria(parametro, tipo);
        }
        return invocation.proceed();
    }

    private void poblarAuditoria(Object parametro, SqlCommandType tipo) {
        UUID actor = currentUserResolver.obtenerUsuarioActualId();
        LocalDateTime ahora = LocalDateTime.now();

        if (tipo == SqlCommandType.INSERT) {
            setSiNull(parametro, "fechaCreacion", ahora);
            setSiNull(parametro, "usuarioCreacion", actor);
            setSiNull(parametro, "estado", (short) 1);
        } else if (tipo == SqlCommandType.UPDATE) {
            setSiNull(parametro, "fechaModificacion", ahora);
            setSiNull(parametro, "usuarioModificacion", actor);
        }
    }

    private void setSiNull(Object obj, String propiedad, Object valor) {
        try {
            Field campo = obtenerCampo(obj.getClass(), propiedad);
            if (campo == null) return;
            campo.setAccessible(true);
            if (campo.get(obj) == null) {
                campo.set(obj, valor);
            }
        } catch (Exception ex) {
            log.debug("No se pudo poblar auditoria '{}' en {}", propiedad, obj.getClass().getSimpleName());
        }
    }

    private Field obtenerCampo(Class<?> clase, String nombre) {
        Class<?> actual = clase;
        while (actual != null && actual != Object.class) {
            try {
                return actual.getDeclaredField(nombre);
            } catch (NoSuchFieldException e) {
                actual = actual.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
