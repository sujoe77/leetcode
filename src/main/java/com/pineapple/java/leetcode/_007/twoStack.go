package main

import (
	"fmt"

	"github.com/golang-collections/collections/stack"
)

var stack1 *stack.Stack
var stack2 *stack.Stack

//use 2 stacks to implement a queue
func main() {
	stack1 = stack.New()
	stack2 = stack.New()
	//max := 10
	n := 0
	for n < 5 {
		n++
		pushQueue(n)
	}
	fmt.Printf("stack1 len is %d\n", stack1.Len())
	fmt.Printf("stack2 len is %d\n", stack2.Len())
	n = 0
	for n < 3 {
		n++
		popQueue()
	}
	fmt.Printf("stack1 len is %d\n", stack1.Len())
	fmt.Printf("stack2 len is %d\n", stack2.Len())
	n = 0
	for n < 2 {
		n++
		popQueue()
	}

}

func pushQueue(e int) {
	fmt.Printf("push %d\n", e)
	stack1.Push(e)
}

func popQueue() int {
	var ret int
	if stack2.Len() > 0 {
		ret = stack2.Pop().(int)
	} else if stack1.Len() > 0 {
		for stack1.Len() > 0 {
			stack2.Push(stack1.Pop().(int))
		}
		ret = stack2.Pop().(int)
	} else {
		ret = -1
	}
	fmt.Printf("pop %d\n", ret)
	return ret
}
