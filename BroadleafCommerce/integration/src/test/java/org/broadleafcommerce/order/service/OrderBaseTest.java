package org.broadleafcommerce.order.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.test.CommonSetupBaseTest;

public class OrderBaseTest extends CommonSetupBaseTest {

    @Resource
    protected CartService cartService;
    
    private int bundleCount = 0;
    
    protected Customer createNamedCustomer() {
    	Customer customer = customerService.createCustomerFromId(null);
    	customer.setUsername(String.valueOf(customer.getId()));
    	return customer;
    }
    
    public Order setUpNamedOrder() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Sku newSku = addTestSku("Small Cube Box", "Cube Box", "Boxes");

        Order order = orderService.createNamedOrderForCustomer("Boxes Named Order", customer);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), newCategory.getId(), 2);
    	
        return order;
    }
    
    public Order setUpAnonymousCartWithInactiveSku() throws PricingException {
        Customer customer = customerService.saveCustomer(createNamedCustomer());

        Order order = cartService.createNewCartForCustomer(customer);

        Sku newSku = addTestSku("Small Plastic Crate", "Plastic Crate", "Crates");
        Sku newInactiveSku = addTestSku("Small Red Plastic Crate", "Plastic Crate", "Crates", false);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
                newProduct.getId(), newCategory.getId(), 2);
        orderService.addSkuToOrder(order.getId(), newInactiveSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
    	
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());
        
        return order;
    }
    
    public Order setUpExistingCartWithInactiveSkuAndInactiveBundle(Customer customer) throws PricingException {
        Sku newSku = addTestSku("Large Plastic Crate", "Plastic Crate", "Crates");
        Sku newInactiveSku = addTestSku("Large Red Plastic Crate", "Plastic Crate", "Crates", false);
        
        Product newProduct = newSku.getAllParentProducts().get(0);
        Category newCategory = newProduct.getDefaultCategory();

        Order order = cartService.createNewCartForCustomer(customer);

        orderService.addSkuToOrder(order.getId(), newSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
        orderService.addSkuToOrder(order.getId(), newInactiveSku.getId(),
        		newProduct.getId(), newCategory.getId(), 2);
        
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequest());
        orderService.addBundleItemToOrder(order, createBundleOrderItemRequestWithInactiveSku());

        return order;
    }

    public Order setUpExistingCart(Customer customer) throws PricingException {
    	Sku newSku = addTestSku("Large Plastic Crate", "Plastic Crate", "Crates");
    	Sku newOtherSku = addTestSku("Large Red Plastic Crate", "Plastic Crate", "Crates");
    	
    	Product newProduct = newSku.getAllParentProducts().get(0);
    	Category newCategory = newProduct.getDefaultCategory();
    	
    	Order order = cartService.createNewCartForCustomer(customer);
    	
    	orderService.addSkuToOrder(order.getId(), newSku.getId(),
    			newProduct.getId(), newCategory.getId(), 2);
    	orderService.addSkuToOrder(order.getId(), newOtherSku.getId(),
    			newProduct.getId(), newCategory.getId(), 2);
    	
    	return order;
    }
    
    public BundleOrderItemRequest createBundleOrderItemRequest() {
        Sku screwSku = addTestSku("Screw", "Bookshelf", "Components");
        Sku shelfSku = addTestSku("Shelf", "Bookshelf", "Components");
        Sku bracketsSku = addTestSku("Brackets", "Bookshelf", "Components");
        Category category = screwSku.getAllParentProducts().get(0).getDefaultCategory();
        
        List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
        discreteOrderItems.add(createDiscreteOrderItemRequest(screwSku, 20));
        discreteOrderItems.add(createDiscreteOrderItemRequest(shelfSku, 3));
        discreteOrderItems.add(createDiscreteOrderItemRequest(bracketsSku, 6));
        
        BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
        itemRequest.setCategory(category);
        itemRequest.setName("test bundle " + bundleCount++);
        itemRequest.setQuantity(1);
        itemRequest.setDiscreteOrderItems(discreteOrderItems);
        return itemRequest;
    }
    
    public BundleOrderItemRequest createBundleOrderItemRequestWithInactiveSku() {
    	Sku drawerSku = addTestSku("Drawer", "Drawer System", "Systems");
    	Sku nailsSku = addTestSku("Nails", "Drawer System", "Systems");
    	Sku tracksSku = addTestSku("Tracks", "Drawer System", "Systems", false);
    	Category category = drawerSku.getAllParentProducts().get(0).getDefaultCategory();
    	
    	List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();
    	discreteOrderItems.add(createDiscreteOrderItemRequest(drawerSku, 20));
    	discreteOrderItems.add(createDiscreteOrderItemRequest(nailsSku, 3));
    	discreteOrderItems.add(createDiscreteOrderItemRequest(tracksSku, 6));
    	
    	BundleOrderItemRequest itemRequest = new BundleOrderItemRequest();
    	itemRequest.setCategory(category);
    	itemRequest.setName("test bundle " + bundleCount++);
    	itemRequest.setQuantity(1);
    	itemRequest.setDiscreteOrderItems(discreteOrderItems);
    	return itemRequest;
    }
    
    public DiscreteOrderItemRequest createDiscreteOrderItemRequest(Sku sku, int quantity) {
    	Product product = sku.getAllParentProducts().get(0);
    	DiscreteOrderItemRequest request = new DiscreteOrderItemRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        request.setProduct(product);
        request.setCategory(product.getDefaultCategory());
        return request;
    }
    
}
