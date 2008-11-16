/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3.0 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*
* You should have received a copy of the GNU General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

//--------------------------------------------------------------------------------------------------
//
// TODO
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------



// Shared Datastructures --------------------------------------------------------------------------

enum DataBits {
  FIVE = 5,
  SIX = 6,
  SEVEN = 7,
  EIGHT = 8
};

enum Parity {
  NONE = 0,
  ODD = 1,
  EVEN = 2
};

enum StopBits {
  ONE = 1,
  TWO = 2,
  ONE_HALF = 9
};


// Vocabulary -------------------------------------------------------------------------------------

typedef enum DataBits  DataBits;
typedef enum StopBits  StopBits;
typedef enum Parity    Parity;

