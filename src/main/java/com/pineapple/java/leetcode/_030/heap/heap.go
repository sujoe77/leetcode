package main

import (
	"fmt"
	"math"
)

//isMin is true, is min on top, other wise max on top
var isMin bool = false

func main() {
	//heapData := [...]int{3, 4, 5, 6, 7, 8, 9}
	heapData := [...]int{9, 8, 7, 6, 5, 4, 3}
	//fmt.Println(get(heapData, 0, 0))
	fmt.Printf("%v\n", heapData)
	/*fmt.Printf("layer of %d is %d\n", 3, getLayer(3))
	fmt.Printf("layer of %d is %d", 3, getPos(3))
	fmt.Printf("parentIndex of %d is %d", 3, getPos(3))
	i := 0
	for i < 7 {
		getParentIndex(i)
		getLeftChildIndex(i)
		i++
	}*/
	//ret, currentIndex := add(heapData[:], 12)
	//fmt.Printf("%v\n, %d", ret, currentIndex)
	ret, currentIndex := remove(heapData[:], 0)
	fmt.Printf("%v\n, %d", ret, currentIndex)
}

func remove(heapData []int, index int) ([]int, int) {
	ret := heapData[:]
	length := cap(heapData)
	currentIndex := index
	childIndex := getLeftChildIndex(currentIndex)
	for childIndex < length {
		if ret[childIndex] > ret[childIndex+1] == isMin {
			childIndex++
		}
		fmt.Printf("switching %d and %d\n", currentIndex, childIndex)
		ret[currentIndex] = ret[childIndex]
		currentIndex = childIndex
		childIndex = getLeftChildIndex(currentIndex)
	}
	ret[currentIndex] = 0
	fmt.Printf("%v\n", ret)
	return ret, currentIndex
}

func add(heapData []int, value int) ([]int, int) {
	ret := heapData[:]
	currentIndex := len(ret) - 1
	parentIndex := getParentIndex(currentIndex)
	ret[currentIndex] = value
	for ret[parentIndex] > value == isMin && currentIndex > 0 {
		fmt.Printf("switching %d and %d\n", currentIndex, parentIndex)
		ret[parentIndex], ret[currentIndex] = ret[currentIndex], ret[parentIndex]
		currentIndex = parentIndex
		parentIndex = getParentIndex(currentIndex)
	}
	return ret, currentIndex
}

func getParentIndex(index int) int {
	ret := int((index - 1) / 2)
	if ret < 0 {
		ret = 0
	}
	fmt.Printf("getParentIndex of %d is %d\n", index, ret)
	return ret
}

func getLeftChildIndex(index int) int {
	ret := index*2 + 1
	fmt.Printf("getLeftChildIndex of %d is %d\n", index, ret)
	return ret
}

func getIndex(layer int, pos int) int {
	return int(math.Pow(2, float64(layer))) - 1 + pos
}

func getLayer(index int) int {
	oneBased := index + 1
	count := -1
	for oneBased != 0 && count < 10 {
		oneBased = oneBased >> 1
		count++
	}
	fmt.Printf("getLayer %d, return %d", index, count)
	return count
}

func getPos(index int) int {
	ret := index - int(math.Pow(2, float64(getLayer(index)))) + 1
	fmt.Printf("getPos %d, return %d", index, ret)
	return ret
}
