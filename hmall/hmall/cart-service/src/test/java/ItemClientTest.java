import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.cart.CartApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = CartApplication.class)
public class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    void testQueryByItems() {
        List<Long> itemIds = List.of(1L, 2L, 3L);
        List<ItemDTO> items = itemClient.queryByItems(itemIds);
        System.out.println(items);
    }
}