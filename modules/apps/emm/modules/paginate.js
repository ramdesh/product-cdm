/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */
 
function Paginate (data, perPage) {
	
	if (!data) throw new Error('Required Argument Missing')
	if (!(data instanceof Array)) throw new Error('Invalid Argument Type')

	this.data = data
	this.perPage = perPage || 10
	this.currentPage = 0
	this.totalPages = Math.ceil(this.data.length / this.perPage)
}

/**
 * Calculates the offset.
 *
 * @return {Number}
 * @api private
 */
 
Paginate.prototype.offset = function () {
	
	return ((this.currentPage - 1) * this.perPage);
}

/**
 * Returns the specified `page`.
 *
 * @param {Number} pageNum
 * @return {Array}
 * @api public
 */
 
Paginate.prototype.page = function (pageNum) {
	
	if (pageNum < 1) pageNum = 1
	if (pageNum > this.totalPages) pageNum = this.totalPages
	
	this.currentPage = pageNum
	
	var start = this.offset()
	  , end = start + this.perPage

	return this.data.slice(start, end);
}

/**
 * Returns the next `page`.
 *
 * @return {Array}
 * @api public
 */
 
Paginate.prototype.next = function () {

	return this.page(this.currentPage + 1);
}

/**
 * Returns the previous `page`.
 *
 * @return {Array}
 * @api public
 */
 
Paginate.prototype.prev = function () {
	
	return this.page(this.currentPage - 1);
}

/**
 * Checks if there is a next `page`.
 *
 * @return {Boolean}
 * @api public
 */
 
Paginate.prototype.hasNext = function () {
	
	return (this.currentPage < this.totalPages)
}

/**
 * Expose `Paginate`
 */
 
if (typeof module !== 'undefined') module.exports = Paginate
