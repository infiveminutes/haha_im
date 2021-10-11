import com.haha.im.ioc.TransferIocConfig;
import com.haha.im.mq.Producer;
import com.haha.im.service.UserStatusService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BaseTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(TransferIocConfig.class);
        UserStatusService userStatusService = applicationContext.getBean(UserStatusService.class);
        Producer producer = applicationContext.getBean(Producer.class);
        System.out.println(userStatusService.getConnectorId("haha"));
    }
}
