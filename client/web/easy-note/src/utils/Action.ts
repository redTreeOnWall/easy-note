type func<T> = (param: T) => void;

export class Action<T>{
  listeners: Set< func<T> > = new Set();
  add(listener: func<T>){
    this.listeners.add(listener);
  }

  remove(listener: func<T>){
    this.listeners.delete(listener);
  }

  invoke(arg: T){
    this.listeners.forEach( listener =>{
      listener(arg);
    });
  }
};
