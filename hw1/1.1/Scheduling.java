import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @author Aman's Alien
 * 
 */
public class Scheduling {

	class Meeting {

		int currentAssigned = 0;
		List<Integer> domain;
		Integer id;

		Meeting(int num_of_time) {
			for (int i = 1; i <= num_of_time; i++) {
				addDomain(i);
			}
		}

		public List<Integer> getDomain() {
			return domain;
		}

		public void setDomain(List<Integer> domain) {
			this.domain = domain;
		}

		public void addDomain(Integer domainVal) {
			if (getDomain() == null) {
				domain = new ArrayList<>();
			}
			getDomain().add(domainVal);
		}

		@Override
		public boolean equals(Object obj) {
			Meeting meeting = (Meeting) obj;

			return (this.id).equals(meeting.id);
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return this.id.hashCode();
		}
	}

	int num_of_meetings;
	int num_of_employees;
	int num_of_time_slots;
	List[] emp_meeting;
	int[][] time_diff;
	List<Meeting> meetings;
	Map<Integer, Set<Integer>> meeting_emp;
	Map<Integer, Set<Integer>> employee_meeting;
	Map<Integer, Integer> attemptedAssignments;

	public void read(String file) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(file));

		for (int i = 0; i < 3; i++) {
			String line = scanner.nextLine();
			int index = line.lastIndexOf(": ");
			switch (i) {
			case 0:
				num_of_meetings = Integer.parseInt(line.substring(index + 2));
				break;
			case 1:
				num_of_employees = Integer.parseInt(line.substring(index + 2));
				break;

			case 2:
				num_of_time_slots = Integer.parseInt(line.substring(index + 2));
				break;
			default:
				break;
			}
		}
		emp_meeting = new ArrayList[num_of_employees + 1];
		scanner.nextLine();
		scanner.nextLine();
		for (int i = 0; i < num_of_employees; i++) {
			List list = new ArrayList();
			String s = scanner.nextLine();
			int in = s.lastIndexOf(": ");
			String[] meetings = s.substring(in + 2).split("  ");
			for (int k = 0; k < meetings.length; k++) {
				list.add(k, Integer.parseInt(meetings[k].trim()));
			}
			emp_meeting[i + 1] = list;

		}
		scanner.nextLine();
		scanner.nextLine();
		scanner.nextLine();
		time_diff = new int[num_of_meetings + 1][num_of_meetings + 1];
		meetings = new ArrayList<>(num_of_meetings + 1);
		meeting_emp = new HashMap<>();
		employee_meeting = new HashMap<>();
		meetings.add(0, null);
		for (int i = 0; i < num_of_meetings; i++) {
			// Intialize Meeting Variable
			Meeting meeting = new Meeting(num_of_time_slots);
			meeting.id = i + 1;
			meetings.add(i + 1, meeting);
			// Parsing
			String s = scanner.nextLine();
			int in = s.lastIndexOf(": ");
			String[] meetings = s.substring(in + 5).split("  ");
			for (int k = 0; k < meetings.length; k++) {
				try {
					String str = meetings[k].trim();
					if (!str.isEmpty())
						time_diff[i + 1][k + 1] = Integer.parseInt(str);
				} catch (Exception e) {
					System.out.println(k + i);
				}
			}

		}
		for (int i = 1; i <= num_of_employees; i++) {
			List emList = emp_meeting[i];
			Set<Integer> meeting = new HashSet<>();
			for (Object object : emList) {
				Integer meeting2 = meetings.get((int) object).id;
				meeting.add(meeting2);
				if (meeting_emp.get(meeting2) == null) {
					Set<Integer> integers = new HashSet<>();
					integers.add(i);
					meeting_emp.put(meeting2, integers);
				} else {
					Set<Integer> integers = meeting_emp.get(meeting2);
					integers.add(i);
				}

			}
			employee_meeting.put(i, meeting);

		}
		System.out.println();
		ArrayList[] a = new ArrayList[num_of_employees + 1];
		for (int i = 0; i < num_of_employees; i++) {
			ArrayList arrayList = new ArrayList();
			for (int j = 1; j <= num_of_time_slots; j++) {
				arrayList.add(j);
			}
			a[i + 1] = arrayList;
		}
		attemptedAssignments = new HashMap<>();
		boolean b = recursiveBacktracking(meetings, a);
		if (b) {
			for (Meeting meeting : meetings) {
				if (meeting != null)
					System.out.println("Meeting " + meeting.id
							+ " is scheduled at time " + meeting.currentAssigned);
			}
			System.out.println("found");
		}

		for (Map.Entry<Integer, Integer> entry : attemptedAssignments
				.entrySet()) {
			System.out.println("Meeting " + entry.getKey()
					+ " was attempted " + entry.getValue() + " times");
		}

	}

	public boolean recursiveBacktracking(List<Meeting> meetings,
			List<Integer>[] domainVal) {

		if (isComplete(meetings)) {
			return true;
		}
		Meeting meeting = findUnassignedMeeting(meetings, domainVal);
		List<Integer> domainValues = domainVal[meeting.id];
		List<Integer> copy = new ArrayList<>();
		copy.addAll(domainValues);
		for (Integer value : copy) {
			// System.out.println("Assigned Meeting with id : " + meeting.id
			// + " value :" + value);

			if (isConsistent(meeting, meetings, value)) {
				meeting.currentAssigned = value;
				List<Integer>[] temp = deepCopy(domainVal);
				Integer count = attemptedAssignments.get(meeting.id);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				attemptedAssignments.put(meeting.id, count);
				if (recursiveBacktracking(meetings, temp)) {
					return true;
				}
				// System.out.println("false assignement");
				// System.out.println("Removed Meeting id : " + meeting.id
				// + " value :" + value);
				if (meeting.currentAssigned == 12 && meeting.id == 17) {
					System.out.println();
				}
				meeting.currentAssigned = 0;
				List<Integer> d = domainVal[meeting.id];
				d.remove(value);
				// meeting.getDomain().remove(value);
			}

		}
		return false;
	}

	private List<Integer>[] deepCopy(List<Integer>[] domainVal) {
		List<Integer>[] a = new ArrayList[domainVal.length];
		a[0] = null;
		for (int i = 1; i < domainVal.length; i++) {
			ArrayList b = new ArrayList<>();
			b.addAll(domainVal[i]);
			a[i] = b;
		}
		return a;
	}

	private Meeting findUnassignedMeeting(List<Meeting> meetings2,
			List<Integer>[] domainVal) {
		int size = Integer.MAX_VALUE;
		Meeting mostConstrained = null;
		for (Meeting meeting : meetings2) {
			if (meeting != null)
				if (domainVal[meeting.id].size() < size
						&& meeting.currentAssigned == 0) {
					size = domainVal[meeting.id].size();
					mostConstrained = meeting;
				}
		}
		return mostConstrained;

		// Meeting meeting = null;
		//
		// for (Meeting meet2 : meetings2) {
		// if (meet2 != null && meet2.currentAssigned == 0) {
		// meeting = meet2;
		// return meeting;
		// }
		// }
		// return meeting;
	}

	private boolean isConsistent(Meeting meeting, List<Meeting> meetings,
			Integer value) {
		return noTwoEmployeeInSameTimeSlot(meeting, meetings, value)
				&& timeBetweenTwoMeeting(meeting, meetings, value);

	}

	private boolean timeBetweenTwoMeeting(Meeting meeting,
			List<Meeting> meetings2, Integer value) {

		Set<Integer> employeesInMeeting = meeting_emp.get(meeting.id);
		for (Integer emp : employeesInMeeting) {
			Set<Integer> meetings = employee_meeting.get(emp);
			for (Integer integer : meetings) {
				Meeting meeting2 = meetings2.get(integer);
				int a = time_diff[meeting.id][meeting2.id];
				if (!meeting2.equals(meeting) && meeting2.currentAssigned != 0
						&& Math.abs(value - meeting2.currentAssigned) <= a) {
					// System.out.println("Conflict due to time");
					return false;
				}
			}
		}
		return true;
	}

	private boolean noTwoEmployeeInSameTimeSlot(Meeting meeting,
			List<Meeting> meetin, Integer value) {
		if (meeting.id == 17 && meeting.currentAssigned == 12) {
			System.out.println();
		}
		Set<Integer> employeesInMeeting = meeting_emp.get(meeting.id);
		for (Integer emp : employeesInMeeting) {
			Set<Integer> meetings = employee_meeting.get(emp);
			for (Integer meeting2 : meetings) {
				Meeting other = meetin.get(meeting2);
				if (other != null && other.currentAssigned != 0
						&& other.currentAssigned == value
						&& !other.equals(meeting)) {

					return false;
				}
			}
		}
		return true;
	}

	private boolean isComplete(List<Meeting> meetings2) {
		for (int i = 1; i < meetings2.size(); i++) {
			if (meetings2.get(i).currentAssigned == 0) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException {
		new Scheduling().read(args[0]);
	}
}
