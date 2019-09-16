package main

import (
	"fmt"
)

//print ugly numbers, brute force
func main() {
	count := 0
	n := 100
	i := 2
	for count < n {
		if isUgly(i) {
			count++
			fmt.Printf("%d:%d\n", count, i)
		}
		i++
	}
}

func isUgly(n int) bool {
	a := n
	for a%2 == 0 {
		a = a / 2
	}
	for a%3 == 0 {
		a = a / 3
	}
	for a%5 == 0 {
		a = a / 5
	}
	return a == 1
}
