package info.jupiter.util;

import info.jupiter.Entity;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
 
/**
 * Basically a highly efficient EntityList implementation which is 
 * backed by an array and uses a queue to provide new indexes.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 * @param <E> The class which extends Entity.
 */
public class EntityList<E extends Entity> implements Iterable<E> {
 
        /**
         * The backing array of all entities active for this instance.
         */
        private final Entity[] entities;
        
        /**
         * A Queue of valid indexes.
         */
        private Queue<Integer> validIndices;
 
        /**
         * The current size of the entitylist.
         */
        private int size = 0;
        
        /**
         * Creates a new EntityList with the specified capacity.
         * @param capacity the capacity to use
         */
        public EntityList(int capacity) {
                entities = new Entity[capacity+1]; // do not use idx 0
                validIndices = new ArrayDeque<Integer>(capacity);
                for (int i =1; i< capacity+1; i++)
                        validIndices.add(i);
        }
        
        /**
         * Gets the entity object associated with a specific index.
         * @param index the index
         * @return The entity object from the list.
         * NOTE: THIS WILL RETURN NULL ENTRIES!
         */
        @SuppressWarnings("unchecked")
        public E get(int index) {
                if(index <= 0 || index >= entities.length) {
                        throw new IndexOutOfBoundsException();
                }
                return (E) entities[index];
        }
        
        /**
         * Adds an entity to the EntityList
         * @param entity the entity to add
         * @return whether the operation was successful or not
         */
        public boolean add(E entity) {
                if(validIndices.peek() == null)
                        return false;
                int i = validIndices.poll();
                entities[i] = entity;
                entity.setIndex(i);
                size++;
                return true;
        }
 
        /**
         * Does the entityList contain an object?
         * @param entity the entity being checked.
         * @return whether the entity is in the list.
         */
        public boolean contains(E entity) {
                return entities[entity.getIndex()] != null;
        }
 
        /**
         * Gets a new EntityList iterator.
         */
        @Override
        public Iterator<E> iterator() {
                return new EntityListIterator();
        }
 
        /**
         * Removes a specific entity from the list
         * @param entity The entity to be removed
         * @return Whether the removal of the entity was successful
         */
        public boolean remove(E entity) {
                if((entity != null) && (entities[entity.getIndex()] == entity)) {
                        entities[entity.getIndex()] = null;
                        validIndices.offer(entity.getIndex());
                        size--;
                        return true;
                }
                return false;
        }
 
        /**
         * Gets the current size of the EntityList.
         * @return the size.
         */
        public int size() {
                return size;
        }
        
        /**
         * Itinerates through the entrys in the EntityList.
         * @author Advocatus <davidcntt@hotmail.com>
         *
         */
        public class EntityListIterator implements Iterator<E> {
 
                /**
                 * The last index which has been itinerated.
                 */
                private int lastIndex = -1;
 
                /**
                 * The last index which has been successfully itinerated.
                 */
                private int cursor = 0;
 
                /**
                 * Does does the entitylist contain another entry?
                 */
                @Override
                public boolean hasNext() {
                        for (int i = cursor; i < entities.length; i++) {
                                if (entities[i] != null)
                                        return true;
                        }
                        return false;
                }
 
                /**
                 * Gets the next entry.
                 */
                @SuppressWarnings("unchecked")
                @Override
                public E next() {
                        for (int i = cursor; i < entities.length; i++) {
                                if (entities[i] != null) {
                                        lastIndex = i;
                                        cursor = i + 1;
                                        return (E) entities[i];
                                }
                        }
 
                        throw new NoSuchElementException();
                }
 
                /**
                 * Removes the entry from the iterator.
                 */
                @SuppressWarnings("unchecked")
                @Override
                public void remove() {
                        if (lastIndex != -1) {
                                E entity = (E) entities[lastIndex];
                                entity.setIndex(-1);
                                entities[lastIndex] = null;
                                lastIndex = -1;
                        }
 
                        throw new IllegalStateException();
                }
        }
}