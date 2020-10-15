public class MyVector<T> {

  private Object[] users;
  private int size;
  private int capacity;

  public MyVector() {
    this.size = 0;
    this.capacity = 10;
    this.users = new Object[capacity];
  }

  public MyVector(int size, T initialValue) {
    this.size = size;
    if (size > capacity) {
      this.capacity = size;
    }
    this.users = new Object[capacity];
    for (int i = 0; i < size; ++i) {
      users[i] = initialValue;
    }
  }

  public void add(T x) {
    if (users.length == size) {
      increaseVector();
    }
    users[size++] = x;
  }

  public void add(int index, T x) {
    size++;
    if (users.length == size) {
      increaseVector();
    }
    for (int i = size - 1; i > 0 && i > index; --i) {
      int previous = i;
      users[i] = users[--previous];
    }
    users[index] = x;
  }

  public T get(int index) {
    return (T) users[index];
  }

  public int capacity() {
    return capacity;
  }

  public int size() {
    return size;
  }

  public void set(int index, T x) {
    if (index > size) {
      increaseVector();
    }
    users[index] = x;
  }

  public T firstElement() {
    return (T) users[0];
  }

  public T lastElement() {
    int index = size;
    return (T) users[--index];
  }

  public void removeLast() {
    --size;
    if (size < capacity / 3) {
      decreaseVector();
    }
  }

  public void removeFirst() {
    for (int i = 1; i < size; ++i) {
      int index = i;
      users[--index] = users[i];
    }
    --size;
    if (size < capacity / 3) {
      decreaseVector();
    }
  }

  public void remove(int index) {
    for (int i = index; i < size; i++) {
      int next = i;
      users[i] = users[++next];
    }
    if (index <= size) {
      --size;
      if (size < capacity / 3) {
        decreaseVector();
      }
    }
  }

  public int indexOf(T x) {
    for (int i = 0; i < size; ++i) {
      if (users[i].equals(x)) {
        return i;
      }
    }
    return -1;
  }

  public int indexOf(int startIndex, T x) {
    for (int i = startIndex; i < size; ++i) {
      if (users[i].equals(x)) {
        return i;
      }
    }
    return -1;
  }

  public int lastIndexOf(Object o) {
    for (int i = size - 1; i >= 0; --i) {
      if (users[i].equals(o)) {
        return i;
      }
    }
    return -1;
  }

  public int lastIndexOf(Object o, int index) {
    for (int i = index; i >= 0; --i) {
      if (users[i].equals(o)) {
        return i;
      }
    }
    return -1;
  }

  public <T> T[] toArray(T[] a) {
    for (int i = 0; i < size; ++i) {
      a[i] = (T) users[i];
    }
    return a;
  }

  public void changeCapacity(int newCapacity) {
    if (size == newCapacity) {
      return;
    }

    if (size > newCapacity) {
      this.size = newCapacity;
      return;
    }

    Object[] newVector = new Object[newCapacity];
    for (int i = 0; i < newCapacity; ++i) {
      if (i < size) {
        newVector[i] = users[i];
      } else {
        break;
      }
    }
    this.users = newVector;

    if (size < capacity / 3) {
      decreaseVector();
    }
  }

  public boolean equals(MyVector v) {
    if (this.size != v.size()) {
      return false;
    }
    for (int i = 0; i < size; ++i) {
      if (!users[i].equals(v.get(i))) {
        return false;
      }
    }
    return true;
  }

  private void increaseVector() {
    capacity *= 4;
    Object[] new_users = new Object[capacity];
    for (int i = 0; i < users.length; ++i) {
      new_users[i] = users[i];
    }
    this.users = new_users;
  }

  private void decreaseVector() {
    capacity /= 2;
    Object[] new_users = new Object[capacity];
    for (int i = 0; i < users.length && i < capacity; ++i) {
      new_users[i] = users[i];
    }
    this.users = new_users;
  }
}