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
    component: LoginPage
  },
  {
    path: "/forget",
    name: "ForgetPassword",
    component: ForgetPassword
  },
  {
    path: "/student",
    component: StudentHome,
    children: [
      {
        path: "",
        name: "StudentWelcome",
        component: StudentWelcome
      },
      {
        path: "assessment/test",
        name: "PsychologyTest",
        component: PsychologyTest
      },
      {
        path: "assessment/record",
        name: "TestRecord",
        component: TestRecord
      },
      {
        path: "appointment/apply",
        name: "AppointmentApply",
        component: AppointmentApply
      },
      {
        path: "appointment/record",
        name: "AppointmentRecord",
        component: AppointmentRecord
      },
      {
        path: "profile",
        name: "StudentProfile",
        component: StudentProfile
      }
    ]
  },
  {
    path: "/teacher",
    component: TeacherHome,
    children: [
      {
        path: "",
        name: "TeacherWelcome",
        component: TeacherWelcome
      },
      {
        path: "schedule/query",
        name: "ScheduleQuery",
        component: ScheduleQuery
      },
      {
        path: "schedule/manage",
        name: "ScheduleManage",
        component: ScheduleManage
      },
      {
        path: "appointment/query",
        name: "TeacherAppointmentQuery",
        component: TeacherAppointmentQuery
      },
      {
        path: "appointment/record",
        name: "TeacherAppointmentRecord",
        component: TeacherAppointmentRecord
      },
      {
        path: "profile",
        name: "TeacherProfile",
        component: TeacherProfile
      }
    ]
  },
  {
    path: "/counselor",
    component: CounselorHome,
    children: [
      {
        path: "",
        name: "CounselorWelcome",
        component: CounselorWelcome
      },
      {
        path: "student/view",
        name: "CounselorStudentView",
        component: CounselorStudentView
      },
      {
        path: "student/warning",
        name: "CounselorStudentWarning",
        component: CounselorStudentWarning
      },
      {
        path: "report",
        name: "CounselorTrendReport",
        component: CounselorTrendReport
      },
      {
        path: "profile",
        name: "CounselorProfile",
        component: CounselorProfile
      }
    ]
  },
  {
    path: "/admin",
    component: AdminHome,
    children: [
      {
        path: "",
        name: "AdminWelcome",
        component: AdminWelcome
      },
      {
        path: "management/counselor",
        name: "CounselorManage",
        component: CounselorManage
      },
      {
        path: "management/teacher",
        name: "AdminTeacherManage",
        component: TeacherManage
      },
      {
        path: "test/input",
        name: "TestInput",
        component: TestInput
      },
      {
        path: "profile",
        name: "AdminProfile",
        component: AdminProfile
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router