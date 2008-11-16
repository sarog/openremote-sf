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
// Header definitions for iodaemon.c
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------

#include "apr_network_io.h"
#include "apr_thread_proc.h"



// Vocabulary -------------------------------------------------------------------------------------

typedef apr_socket_t      *Socket;
typedef Socket            *SocketResult;
typedef apr_sockaddr_t    *SocketAddress;
typedef apr_pool_t        *MemPool;
typedef MemPool           *MemPoolResult;
typedef apr_thread_t      *Thread;
typedef apr_threadattr_t  *ThreadAttributes;
typedef apr_status_t       Status;

