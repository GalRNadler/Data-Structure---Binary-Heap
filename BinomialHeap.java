
//final version :)

/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
    public int size;
    public HeapNode last;
    public HeapNode min;
    public int numberOfTrees;

    /**
     *
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     *
     */
    public HeapItem insert(int key, String info){

        //new node+item

        HeapItem item = new HeapItem();
        item.info = info;
        item.key = key;
        HeapNode node = new HeapNode();
        item.connect(node);

        // check if Heap has B0
        if (size%2 == 0 && !this.empty()){
            item.node.next = last.next;
            last.next = item.node;
            size += 1;
            numberOfTrees += 1;
            if (key < min.item.key){
                min = item.node;
            }
            return item;
        }
        //initiate new heap
        BinomialHeap new_heap = new BinomialHeap();
        new_heap.min = node;
        new_heap.last = node;
        new_heap.last.next = node;
        new_heap.size = 1;
        new_heap.numberOfTrees = 1;

        //meld it and return
        this.meld(new_heap);
        return item;
    }

    /**
     *
     * Delete the minimal item
     *
     */
    public void deleteMin() {
        if(this.size == 0) return;
        //only b-0


        if (this.size == 1){
            this.min = null;
            this.last = null;
            this.size = 0;
            this.numberOfTrees = 0;
            return;
        }
        //b0 with other roots
        HeapNode min = this.min;

        if (min.child == null){
            HeapNode b1 = min.next;
            min.next = null;
            this.last.next = b1;
            this.update_min();
            this.size--;
            this.numberOfTrees-=1;
            return;
        }
        //other cases
        else{
            BinomialHeap children_heap = remove_root(this.min);
            this.meld(children_heap);
        }
    }

    /**
     *
     * Return the minimal HeapItem
     *
     */
    public HeapItem findMin()
    {
        if (this.size==0) return null;
        return this.min.item;
    }

    /**
     *
     * pre: 0<diff<item.key
     *
     * Decrease the key of item by diff and fix the heap.
     *
     */
    public void decreaseKey(HeapItem item, int diff)
    {
        item.key -= diff;
        int a = item.key;
        HeapItem item1 = item;
        HeapNode item_node = item1.node;
        HeapNode item_parent_node = item_node.parent;
        HeapItem item_parent = null;
        if (item_parent_node != null) {
            item_parent = item_parent_node.item;
        }

        while (item_parent_node != null && item_parent.key > item1.key){
            item1.connect(item_parent_node);
            item_parent.connect(item_node);
            item_node = item_node.parent;
            item_parent_node = item_parent_node.parent;
            item1 = item_node.item;
            if (item_parent_node != null) {
                item_parent = item_parent_node.item;
            }
        }
        if (this.min.item.key > a){
            this.min = item_node;
        }
    }

    /**
     *
     * Delete the item from the heap.
     *
     */
    public void delete(HeapItem item)
    {
        int diff = item.key + 1;
        decreaseKey(item, diff);
        this.deleteMin();
    }

    /**
     *
     * Meld the heap with heap2
     *
     */
    public void meld(BinomialHeap heap2)
    {

        //handle if both are empty

        if (this.size==0 && heap2.size==0) {
            return;
        }

        //handle if one of them is empty

        if (this.size==0 || heap2.size==0){
            if (this.size>0) {
                heap2 = new BinomialHeap();
            }
            else {
                this.min = heap2.min;
                this.last = heap2.last;
                this.size = heap2.size;
                this.numberOfTrees = heap2.numberOfTrees;
                heap2.min = null;
                heap2.last = null;
                heap2.size = 0;
                heap2.numberOfTrees = 0;
            }
            return;
        }

        //handle other cases
        //add root lists and sort
        sort(this , heap2);

        //link the trees
        HeapNode a = this.last;
        HeapNode b = a.next;
        HeapNode c = b.next;
        HeapNode d = c.next;

        while (c.rank >= b.rank){


            //handle 2 roots at the end
            if (a==c && b==d){
                if (b.rank != c.rank) break;
                else{
                    HeapNode res = link(b,c);
                    this.last = res;
                    this.min = res;
                    this.update_numtrees();
                    heap2.min = null;
                    heap2.last = null;
                    heap2.size = 0;
                    return;
                }
            }

            //the two last trees of the same rank

            else if (a.rank == b.rank && c.rank == d.rank && b.rank == c.rank){
                a.next = null;
                b.next  = null;
                c.next = null;
                HeapNode tmp = link(b,c);
                a.next = tmp;
                tmp.next = d;
                b = tmp;
                c = b.next;
                d = c.next;


            }

            else if (b.rank == c.rank && c.rank != d.rank){
                b.next = null;
                c.next = null;
                a.next = null;
                HeapNode tmp = link(b,c);
                a.next = tmp;
                tmp.next = d;
                b = tmp;
                c = b.next;
                d = c.next;

            }
            //there are still more trees in that rank
            else{
//            else if (b.rank == c.rank && c.rank == d.rank){
                a = b;
                b = c;
                c = d;
                d = d.next;

            }
            this.last = b;
            this.min = b;
        }

        //updating last, min&size was updated in sort
        update_last();
        update_min();
        update_numtrees();
        heap2.min = null;
        heap2.last = null;
        heap2.size = 0;

    }

    /**
     *
     * Return the number of elements in the heap
     *
     */
    public int size()
    {
        return this.size;
    }

    /**
     *
     * The method returns true if and only if the heap
     * is empty.
     *
     */
    public boolean empty()
    {
        return this.size == 0;
    }

    /**
     *
     * Return the number of trees in the heap.
     *
     */
    public int numTrees()
    {
        return this.numberOfTrees;
    }


    /**
     * Class implementing a node in a Binomial Heap.
     *
     */
    public static class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode parent;
        public int rank;
    }


    /**
     * Class implementing an item in a Binomial Heap.
     *
     */
    public class HeapItem{
        public HeapNode node;
        public int key;
        public String info;

        public void connect(HeapNode node){
            this.node = node;
            node.item = this;
        }
    }

    /////// our methods /////////

    /**
     *
     * The method updates min and returns it
     *
     *
     */

    public HeapNode update_min(){
        if (this.size == 0){
            return null;
        }
        HeapNode check = this.last;
        HeapNode tmp = check.next;
        this.min = check;
        while ( tmp != check ){
            if (tmp.item.key < this.min.item.key){
                this.min = tmp;
            }
            tmp = tmp.next;
        }
        return this.min;
    }

    /**
     *
     * The method updates last and returns it
     *
     *
     */

    public HeapNode update_last() {
        HeapNode tmp1 = this.last;
        HeapNode tmp2 = tmp1.next;
        while (tmp1.rank <= tmp2.rank) {
            if (tmp1.rank == tmp2.rank && tmp2.next == tmp1) {
                break;
            }
            tmp1=tmp1.next;
            tmp2=tmp2.next;
        }
        this.last = tmp1 ;
        return this.last;

    }
    public void update_numtrees() {
        if (this.empty()){
            this.numberOfTrees = 0;
            return;
        }
        int cnt = 1;
        HeapNode tmp = this.last.next;
        while (tmp != this.last){
            cnt++;
            tmp = tmp.next;
        }
        this.numberOfTrees = cnt;
    }


    /**
     *
     * The method ditaches a given root from root list and children
     * creates and returns children heap
     *
     */


    public BinomialHeap remove_root(HeapNode deleted) {

        // detaching deleted from root list
        int deleted_rank = deleted.rank;
        HeapNode after_deleted = deleted.next;
        int after_deleted_rank = after_deleted.rank;
        boolean update_last = false;
        if (deleted_rank>after_deleted_rank){
            update_last = true;
        }
        HeapNode tmp = deleted.next;
//      heap has 1 root
        if (deleted_rank == after_deleted_rank){
            this.last = deleted.child;
            this.min = deleted.child; //will update later
        }

        else{
            while (tmp.next != deleted){
                tmp = tmp.next;
            }
            tmp.next = after_deleted;
            deleted.next = null;}

        //updating fields of this

        if (update_last){
            this.last = tmp;
        }
        this.min = update_min();//not sure if needs to be here or after sorted
        int change = (int) Math.pow(2,deleted_rank);
        this.size -= change;

        //detach from children and create binominal heap
        HeapNode tmp_child = deleted.child.next;
        for (int i = 0; i < deleted_rank; i++) {
            tmp_child.parent = null;
            tmp_child = tmp_child.next;
        }
        BinomialHeap new_roots = new BinomialHeap();
        new_roots.last = deleted.child;
        new_roots.min = deleted.child;
        deleted.child = null;
        new_roots.size = change-1;
        new_roots.numberOfTrees = deleted_rank;
        new_roots.update_min();

        return new_roots;
    }

    /**
     *
     * The method sorts two binomial heaps into a sorted one
     *
     *
     */




    public void sort (BinomialHeap heap1, BinomialHeap heap2) {

        //get the 2 minimal nodes of each heap
        HeapNode a = heap1.last.next;
        int rank_a = a.rank;
        HeapNode b = a.next;
        boolean ab = (a == b);
        HeapNode c = heap2.last.next;
        int rank_c = c.rank;
        HeapNode d = c.next;
        boolean cd = (c == d);
        HeapNode first = a;
        if (rank_a > rank_c) first = c;

        if (cd && ab) { //heap1 & heap 2 has 1 root
            c.next = a;
            a.next = c;
        }

        else if (ab) { //heap1 has 1 root
            if (rank_a <= rank_c || rank_a >= heap2.last.rank ){
                a.next = c;
                heap2.last.next = a;
            }
            else {
                while (c.rank < rank_a && d.rank < rank_a) {
                    c = d;
                    d = d.next;
                }
                c.next = a;
                a.next = d;
            }
        }
        else if (cd) { //heap2 has 1 root
            if (rank_c <= rank_a || rank_c >= heap1.last.rank){
                c.next = a;
                heap1.last.next = c;
            }
            else {
                while (a.rank < rank_c && b.rank < rank_c) {
                    a = b;
                    b = b.next;
                }
                a.next = c;
                c.next = b;
            }
        }
        else{
            if (heap1.last.rank <= rank_c || heap2.last.rank <= rank_a){ //all heap2 > heap1 or all heap2 < heap1
                heap1.last.next = c;
                heap2.last.next = a;
            }
            else{
                while (c != heap2.last && a != heap1.last) { //as long as the 2nd heap isnt finished

                    if (a.rank <= c.rank && b.rank >= c.rank){
                        a.next = c;
                        c.next = b;
                        a = a.next;
                        b = a.next;
                        c = d;
                        d = d.next;
                    }
                    else if (b.rank < c.rank){
                        a = b;
                        b = b.next;
                    }
                    else if (a.rank > c.rank){
                        c.next = a;
                        b = a;
                        a = c;
                        c = d;
                        d = c.next;
                    }
                }
                if (a==heap1.last && c== heap2.last){
                    if (a.rank >= c.rank){
                        c.next = a;
                        a.next = first;
                    }
                    else {
                        a.next = c;
                        c.next = first;
                    }
                }
                else if (a == heap1.last){
                    if (a.rank >= heap2.last.rank) {
                        heap2.last.next = a;
                        heap1.last.next = first;
                    }
                    else{
                        while (a.rank > d.rank){
                            c = d;
                            d = d.next;
                        }
                        a.next = c;
                        heap2.last.next = first;
                    }
                }
                else if (c == heap2.last){
                    if (c.rank >= heap1.last.rank) {
                        heap1.last.next = c;
                        heap2.last.next = first;
                    }
                    else {
                        while (c.rank > b.rank) {
                            a = b;
                            b = b.next;
                        }
                        a.next = c;
                        c.next = b;
                        heap1.last.next = first;
                    }
                }
            }
        }
        //updating size, min, last

        heap1.update_last();
        heap1.update_min();
        heap1.size += heap2.size;
        heap2.size = heap1.size;
    }

    /**
     *
     * links two same rank binomial
     * pre-node 1 and node 2 disconnected from root lists
     *
     */


    public HeapNode link(HeapNode node1,HeapNode node2) {
        //assign nodes to pointers x,y (need to check if assignment can change next and such)
        HeapNode x = node1;
        HeapNode y = node2;
        if (node1.item.key > node2.item.key) {
            x = node2;
            y = node1;
        }
        //connect y as x's child
        y.parent = x;
        if (x.child == null){
            x.child = y;
            x.child.next = x.child;
        }
        else {
            HeapNode first = x.child.next;
            x.child.next = y;
            y.next = first;
            x.child = y;
        }
        // fix x's rank
        x.next = x;
        x.rank+=1;
        return x;
    }

}