package model;


import java.util.ArrayList;

public class Inventory {
    private ItemStack inHand;
    private ArrayList<ItemStack> items;

    /**
     * Este metodo es un inicializador del inventario
     */
    public Inventory(){
        inHand = null;
        items = new ArrayList <ItemStack>();
    }

    /**
     * Esta funcion crea una copia a partir del inventario pasado
     * @param inv con este parametro cogemos los datos para copiarlos
     */
    public Inventory(Inventory inv){

        inHand = inv.getItemInHand();
        items = new ArrayList <>(inv.items);
    }
    /**
     * Esta funcion anyade un item  de una copia
     * @param itemss es la copia
     *
     * @return devuelve la cantidad del item
     */
    public int addItem(ItemStack itemss){
        boolean flag = true;
        for (ItemStack item : items) {
            if(item.getType() == itemss.getType()){
                try {
                    item.setAmount(item.getAmount() + itemss.getAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                flag = false;
            }
        }
        if (flag)
        items.add(itemss);
        return itemss.getAmount();
    }

    /**
     * Limpia el inventario
     */
    public void clear(){
        items.clear();
        inHand = null;
    }

    /**
     * Limpia el inventario en una zona especifica
     * @param slot es la posicion
     * @throws Exception es la excepcion
     */
    public void clear(int slot) throws Exception{
        Boolean existe=false;
        for(int i = 0; i < items.size(); i++){
            if(i == slot){
                items.remove(i);
                existe=true;
            }
        }
        if(existe == false){
            throw new Exception();
        }
    }

    /**
     * devuelve el primer del inventario
     * @param type es el tipo del material
     * @return j la posicion
     */
    public int first(Material type){
        int j = -1;
        Boolean encontrado = false;
        for(int i = 0; i < items.size() && !encontrado; i++){
            if(type == items.get(i).getType()){
                encontrado = true;
                j = i;
            }
        }
        return j;
    }

    /**
     * devuelve el item del inventario
     * @param slot se le pasa la posicion
     * @return items.get(slot) el item en esa posicion
     */
    public ItemStack getItem (int slot){
        if(items.size()<=slot){
            return null;
        }
        if(slot<0){
            return null;
        }
        return items.get(slot);
    }

    /**
     * devuelve el item en la mano
     * @return inHand, lo que tiene en la mano
     */
    public ItemStack getItemInHand(){
        return inHand;
    }

    /**
     * devuelve el tamanyo del inventario
     * @return items.size() el tamanyo
     */
    public int getSize(){
        return items.size();
    }

    /**
     * selecciona el item de una posicion
     * @param slot es la posicion que ocupa el item
     * @param items son los items que contiene
     * @throws Exception esta es la excepcion
     */
    public void setItem(int slot, ItemStack items) throws Exception{
        if(this.items.size() > slot && slot >=0){
            this.items.set(slot,items);
        } else {
            throw new Exception();
        }
    }

    /**
     * Pone un item en la mano
     * @param items esto son los items que pone en la mano
     */
    public void setItemInHand(ItemStack items){
        inHand = items;
    }
}
