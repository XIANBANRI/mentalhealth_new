import { createRouter, createWebHistory } from "vue-router"

import LoginPage from "@/views/LoginPage.vue"
import ForgetPassword from "@/views/ForgetPassword.vue"

import StudentHome from "@/views/student/StudentHome.vue"
import StudentWelcome from "@/views/student/StudentWelcome.vue"
import PsychologyTest from "@/views/student/assessment/PsychologyTest.vue"
import TestRecord from "@/views/student/assessment/TestRecord.vue"
import AppointmentApply from "@/views/student/appointment/AppointmentApply.vue"
import AppointmentRecord from "@/views/student/appointment/AppointmentRecord.vue"
import StudentProfile from "@/views/student/profile/StudentProfile.vue"

import TeacherHome from "@/views/teacher/TeacherHome.vue"
import TeacherWelcome from "@/views/teacher/TeacherWelcome.vue"
import ScheduleQuery from "@/views/teacher/schedule/ScheduleQuery.vue"
import ScheduleManage from "@/views/teacher/schedule/ScheduleManage.vue"
import TeacherAppointmentQuery from "@/views/teacher/appointment/AppointmentQuery.vue"
import TeacherAppointmentRecord from "@/views/teacher/appointment/AppointmentRecord.vue"
import TeacherProfile from "@/views/teacher/profile/TeacherProfile.vue"

import CounselorHome from "@/views/counselor/CounselorHome.vue"
import CounselorWelcome from "@/views/counselor/CounselorWelcome.vue"
import CounselorStudentView from "@/views/counselor/student/StudentView.vue"
import CounselorStudentWarning from "@/views/counselor/student/StudentWarning.vue"
import CounselorTrendReport from "@/views/counselor/report/TrendReport.vue"
import CounselorProfile from "@/views/counselor/profile/CounselorProfile.vue"

import AdminHome from "@/views/admin/AdminHome.vue"
import AdminWelcome from "@/views/admin/AdminWelcome.vue"
import CounselorManage from "@/views/admin/management/CounselorManage.vue"
import TeacherManage from "@/views/admin/management/TeacherManage.vue"
import TestInput from "@/views/admin/test/TestInput.vue"
import AdminProfile from "@/views/admin/profile/AdminProfile.vue"

const routes = [
  {
    path: "/",
    name: "Login",
    component: LoginPage,
    meta: { guestOnly: true }
  },
  {
    path: "/forget",
    name: "ForgetPassword",
    component: ForgetPassword,
    meta: { guestOnly: true }
  },
  {
    path: "/student",
    component: StudentHome,
    meta: { requiresAuth: true, role: "student" },
    children: [
      {
        path: "",
        name: "StudentWelcome",
        component: StudentWelcome,
        meta: { requiresAuth: true, role: "student" }
      },
      {
        path: "assessment/test",
        name: "PsychologyTest",
        component: PsychologyTest,
        meta: { requiresAuth: true, role: "student" }
      },
      {
        path: "assessment/record",
        name: "TestRecord",
        component: TestRecord,
        meta: { requiresAuth: true, role: "student" }
      },
      {
        path: "appointment/apply",
        name: "AppointmentApply",
        component: AppointmentApply,
        meta: { requiresAuth: true, role: "student" }
      },
      {
        path: "appointment/record",
        name: "AppointmentRecord",
        component: AppointmentRecord,
        meta: { requiresAuth: true, role: "student" }
      },
      {
        path: "profile",
        name: "StudentProfile",
        component: StudentProfile,
        meta: { requiresAuth: true, role: "student" }
      }
    ]
  },
  {
    path: "/teacher",
    component: TeacherHome,
    meta: { requiresAuth: true, role: "teacher" },
    children: [
      {
        path: "",
        name: "TeacherWelcome",
        component: TeacherWelcome,
        meta: { requiresAuth: true, role: "teacher" }
      },
      {
        path: "schedule/query",
        name: "ScheduleQuery",
        component: ScheduleQuery,
        meta: { requiresAuth: true, role: "teacher" }
      },
      {
        path: "schedule/manage",
        name: "ScheduleManage",
        component: ScheduleManage,
        meta: { requiresAuth: true, role: "teacher" }
      },
      {
        path: "appointment/query",
        name: "TeacherAppointmentQuery",
        component: TeacherAppointmentQuery,
        meta: { requiresAuth: true, role: "teacher" }
      },
      {
        path: "appointment/record",
        name: "TeacherAppointmentRecord",
        component: TeacherAppointmentRecord,
        meta: { requiresAuth: true, role: "teacher" }
      },
      {
        path: "profile",
        name: "TeacherProfile",
        component: TeacherProfile,
        meta: { requiresAuth: true, role: "teacher" }
      }
    ]
  },
  {
    path: "/counselor",
    component: CounselorHome,
    meta: { requiresAuth: true, role: "counselor" },
    children: [
      {
        path: "",
        name: "CounselorWelcome",
        component: CounselorWelcome,
        meta: { requiresAuth: true, role: "counselor" }
      },
      {
        path: "student/view",
        name: "CounselorStudentView",
        component: CounselorStudentView,
        meta: { requiresAuth: true, role: "counselor" }
      },
      {
        path: "student/warning",
        name: "CounselorStudentWarning",
        component: CounselorStudentWarning,
        meta: { requiresAuth: true, role: "counselor" }
      },
      {
        path: "report",
        name: "CounselorTrendReport",
        component: CounselorTrendReport,
        meta: { requiresAuth: true, role: "counselor" }
      },
      {
        path: "profile",
        name: "CounselorProfile",
        component: CounselorProfile,
        meta: { requiresAuth: true, role: "counselor" }
      }
    ]
  },
  {
    path: "/admin",
    component: AdminHome,
    meta: { requiresAuth: true, role: "admin" },
    children: [
      {
        path: "",
        name: "AdminWelcome",
        component: AdminWelcome,
        meta: { requiresAuth: true, role: "admin" }
      },
      {
        path: "management/counselor",
        name: "CounselorManage",
        component: CounselorManage,
        meta: { requiresAuth: true, role: "admin" }
      },
      {
        path: "management/teacher",
        name: "AdminTeacherManage",
        component: TeacherManage,
        meta: { requiresAuth: true, role: "admin" }
      },
      {
        path: "test/input",
        name: "TestInput",
        component: TestInput,
        meta: { requiresAuth: true, role: "admin" }
      },
      {
        path: "profile",
        name: "AdminProfile",
        component: AdminProfile,
        meta: { requiresAuth: true, role: "admin" }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function getHomePathByRole(role) {
  switch (role) {
    case "student":
      return "/student"
    case "teacher":
      return "/teacher"
    case "counselor":
      return "/counselor"
    case "admin":
      return "/admin"
    default:
      return "/"
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token")
  const role = localStorage.getItem("role")

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const guestOnly = to.matched.some(record => record.meta.guestOnly)

  if (requiresAuth && !token) {
    return next("/")
  }

  if (guestOnly && token) {
    return next(getHomePathByRole(role))
  }

  const targetRoleRecord = [...to.matched].reverse().find(record => record.meta.role)
  const targetRole = targetRoleRecord?.meta?.role

  if (targetRole && role && targetRole !== role) {
    alert("无权限访问该页面")
    return next(getHomePathByRole(role))
  }

  next()
})

export default router