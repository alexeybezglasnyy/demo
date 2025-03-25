// Language: java
package com.example.demo.warehouse;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WarehouseOrderService {

    @Transactional
    public void splitOrder() {
        // todo: finish the implementation
        // create child order
        // come up with a way to notify the core (considering it's a separate service)


    }
}