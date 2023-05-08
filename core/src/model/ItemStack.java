package model;

public class ItemStack {

    private int amount;

    /**
     * MAX_STACK_SIZE es el maximo valor de items por stack
     */
    public static final int MAX_STACK_SIZE = 1000;
    private Material type;

    /**
     * Es el contructor del ItemStack
     * @param type es el tipo de material que se le pasa al itemstack
     * @param amount es la cantidad que recibe el itemstack
     * @throws Exception es la excepcion de limite alcanzado
     */
    public ItemStack(Material type, int amount) throws Exception{

        if(type.isWeapon() || type.isTool()){
            if(amount == 1){
                this.amount = amount;
                this.type = type;
            }
            else{
                throw new Exception();
            }
        }
        else if(type.isEdible() || type.isBlock() || type.isLiquid()){
            if(amount > 0 && amount <= MAX_STACK_SIZE){
                this.amount = amount;
                this.type = type;
            }
            else{
                throw new Exception();
            }
        }
    }

    /**
     * Es un constructor de copia
     * @param Item es el item de copia
     */
    public ItemStack(ItemStack Item){
        this.amount = Item.getAmount();
        this.type = Item.getType();
    }

    /**
     * devuelve la cantidad
     * @return amount es decir la cantidad
     */
    public int getAmount() {
        return amount;
    }

    /**
     * devuelve el tipo del material que se devuelve
     * @return type el tipo
     */
    public Material getType() {
        return type;
    }

    /**
     * pone la cantidad del tipo
     * @param amount es la cantidad
     * @throws Exception devuelve esta excepcion si pasa el tamanyo del stack
     */
    public void setAmount(int amount) throws Exception{

        if(getType().isWeapon() || getType().isTool()){
            if(amount == 1){
                this.amount = amount;
            }
            else{
                throw new Exception();
            }
        }
        else if(getType().isEdible() || getType().isBlock()){
            if(amount > 0 && amount <= MAX_STACK_SIZE){
                this.amount = amount;
            }
            else{
                throw new Exception();
            }
        }
    }
}
