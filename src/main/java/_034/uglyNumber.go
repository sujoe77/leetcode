package main

import (
	"fmt"
	"math"
)

//print ugly numbers
func main() {
	max := 4
	n := 0
	for n <= max {
		i := 0
		for i <= n {
			j := 0
			for j <= n-i {
				fmt.Printf("%d\n", int(math.Pow(5, float64(i))*math.Pow(3, float64(j))*math.Pow(2, float64(n-i-j))))
				j++
			}
			i++
		}
		n++
	}
}
